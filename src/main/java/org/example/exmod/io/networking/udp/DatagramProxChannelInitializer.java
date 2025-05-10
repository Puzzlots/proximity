
package org.example.exmod.io.networking.udp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import org.example.exmod.io.networking.encoding.ByteArrayDataEncoder;
import org.example.exmod.io.networking.encoding.ByteArrayDataDecoder;

public class DatagramProxChannelInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    protected void initChannel(DatagramChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new ByteArrayDataEncoder());
        pipeline.addLast(new ByteArrayDataDecoder());
        pipeline.addLast(new UDPPacketHandler());
    }

}
