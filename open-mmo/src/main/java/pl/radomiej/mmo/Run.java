package pl.radomiej.mmo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import pl.radomiej.mmo.network.UdpGameEventHandler;

public class Run {

	private static final int PORT = 9123;

	public static void main(String[] args) throws IOException {
		RunGame();
		
		RunNetwork();
		
	}

	private static void RunGame() {
		BasicGameEngine.INSTANCE.start();
		BasicNetworkEngine.INSTANCE.start();
	}

	private static void RunNetwork() throws IOException {
		NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(new UdpGameEventHandler());
		
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
//		chain.addLast("logger", new LoggingFilter());
		
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
		dcfg.setReaderIdleTime(1);
		dcfg.setBothIdleTime(1);
		dcfg.setWriterIdleTime(1);
		dcfg.setWriteTimeout(1);
		
		dcfg.setCloseOnPortUnreachable(true);
		
		
		acceptor.bind(new InetSocketAddress(PORT));		
	}

}
