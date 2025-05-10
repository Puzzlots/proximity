package org.example.exmod.io.networking;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.example.exmod.io.networking.encoding.ByteArrayDataEncoder;
import org.example.exmod.io.networking.encoding.ByteArrayDataDecoder;
import org.example.exmod.io.networking.tcp.TCPPacketHandler;

public class ProxChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new ByteArrayDataEncoder());
        pipeline.addLast(new ByteArrayDataDecoder());
        pipeline.addLast(new TCPPacketHandler());
    }

}
