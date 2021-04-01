package the.flash;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServer {

    private static final int BEGIN_PORT = 8000;

    public static void main(String[] args) {
        //监听端口，接收新连接的线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理每一条连接的数据读写的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        //引导类--引导我们进行服务器端的启动工作
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");
        serverBootstrap
                //配置两大线程组，定型引导类的线程模型
                .group(bossGroup, workerGroup)
                //指定我们服务端的IO模型为NIO
                .channel(NioServerSocketChannel.class)
                //为服务端channel(NioServerSocketChannel)指定自定义属性
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                //为每一条连接指定自定义属性
                .childAttr(clientKey, "clientValue")
                //为服务端channel(NioServerSocketChannel)设置一些属性，设置临时存放已完成握手请求的队列最大长度
                .option(ChannelOption.SO_BACKLOG, 1024)
                //给每一条连接指定一些TCP底层相关的属性，开启TCP底层心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //开启Nagle算法
                .childOption(ChannelOption.TCP_NODELAY, true)

                /*
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println(ch.attr(clientKey).get());
                    }
                });
                */
                //泛型参数NioSocketChannel, Netty对NIO类型的连接的抽象
                //调用childHandler()方法，给这个引导类创建一个ChannelInitializer,这里主要
                //定义后续每条连接的数据读写，业务处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>(){
                   protected void initChannel(NioSocketChannel ch){

                   }
                });
        //要启动一个Netty服务端，必须要指定三类属性：
        //      1.线程模型  2.IO模型  3. 连接读写逻辑
        //最后调用bind()就可以在本地绑定一个端口启动起来


        bind(serverBootstrap, BEGIN_PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
                bind(serverBootstrap, port + 1);    //在端口绑定失败之后，重新调用自身方法，并且把端口号加1
            }
        });
    }
}
