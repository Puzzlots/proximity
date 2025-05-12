
package io.github.Puzzlots.Proximity.io.networking.protocol.udp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.github.Puzzlots.Proximity.io.networking.encoding.ByteArrayDataEncoder;
import io.github.Puzzlots.Proximity.io.networking.encoding.ByteArrayDataDecoder;

public class DatagramProxChannelInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    protected void initChannel(DatagramChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new ByteArrayDataEncoder());
        pipeline.addLast(new ByteArrayDataDecoder());
        pipeline.addLast(new UDPPacketHandler());
    }

}
