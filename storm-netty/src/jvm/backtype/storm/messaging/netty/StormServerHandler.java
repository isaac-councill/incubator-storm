package backtype.storm.messaging.netty;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.messaging.TaskMessage;

class StormServerHandler extends SimpleChannelUpstreamHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(StormServerHandler.class);
    Server server;
    private AtomicInteger failure_count; 
    
    StormServerHandler(Server server) {
        this.server = server;
        failure_count = new AtomicInteger(0);
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
	final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
	try{
	    ChannelFuture handshakeFuture = sslHandler.handshake();
	    handshakeFuture.addListener(new Greeter(sslHandler, server));
	}catch(Exception exc) {
	    exc.printStackTrace();
	}
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Object msg = e.getMessage();  
        if (msg == null) return;

        //end of batch?
        if (msg==ControlMessage.EOB_MESSAGE) {
            Channel channel = ctx.getChannel();
            LOG.debug("Send back response ...");
            if (failure_count.get()==0)
                channel.write(ControlMessage.OK_RESPONSE);
            else channel.write(ControlMessage.FAILURE_RESPONSE);
            return;
        }
        
        //enqueue the received message for processing
        try {
            server.enqueue((TaskMessage)msg);
        } catch (InterruptedException e1) {
            LOG.info("failed to enqueue a request message", e);
            failure_count.incrementAndGet();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	e.getCause().printStackTrace();
        server.closeChannel(e.getChannel());
    }

    private static final class Greeter implements ChannelFutureListener {
	
	private final SslHandler sslHandler;
	private final Server server;

	Greeter(SslHandler sslHandler, Server server) {
	    this.sslHandler = sslHandler;
	    this.server = server;
	}

	public void operationComplete(ChannelFuture future) throws Exception {
	    if (future.isSuccess()) {
		server.addChannel(future.getChannel());
	    } else {
		future.getChannel().close();
	    }
	}
    }
}
