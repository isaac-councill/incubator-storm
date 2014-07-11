package backtype.storm.messaging.netty;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.ssl.SslHandler;

class StormClientPipelineFactory implements ChannelPipelineFactory {
    private Client client;

    StormClientPipelineFactory(Client client) {
        this.client = client;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // SSL everywhere.
        SSLEngine engine = StormSslContextFactory.getClientContext().createSSLEngine();
        engine.setUseClientMode(true);
        engine.setEnabledCipherSuites(engine.getSupportedCipherSuites());

        pipeline.addLast("ssl", new SslHandler(engine));

        // Decoder
        pipeline.addLast("decoder", new MessageDecoder());
        // Encoder
        pipeline.addLast("encoder", new MessageEncoder());
        // business logic.
        pipeline.addLast("handler", new StormClientHandler(client));

        return pipeline;
    }
}
