package com.criss.wang.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/*
 * 一、通道（Channel）：用于源节点与目标节点的连接。在 Java NIO 中负责缓冲区中数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输。
 * 
 * 二、通道的主要实现类
 * 	java.nio.channels.Channel 接口：
 * 		|--FileChannel
 * 		|--SocketChannel
 * 		|--ServerSocketChannel
 * 		|--DatagramChannel
 * 
 * 三、获取通道
 * 1. Java 针对支持通道的类提供了 getChannel() 方法
 * 		本地 IO：
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 * 
 * 		网络IO：
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 		
 * 2. 在 JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 * 3. 在 JDK 1.7 中的 NIO.2 的 Files 工具类的 newByteChannel()
 * 
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 * 
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads）：将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）：将多个缓冲区中的数据聚集到通道中
 * 
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组  -> 字符串
 * 
 */
public class TestBuffer {

	public static void main(String[] args) {
		test1();
		test2();
	}

	/**
	 * 分散和聚集
	 */
	public static void test1() {
		try {
			RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");
			// 獲取通道
			FileChannel channel1 = raf1.getChannel();

			// 分配指定大小的缓冲区
			ByteBuffer buf1 = ByteBuffer.allocate(100);
			ByteBuffer buf2 = ByteBuffer.allocate(1024);

			// 分散读取
			ByteBuffer[] bufs = { buf1, buf2 };
			channel1.read(bufs);

			for (ByteBuffer byteBuffer : bufs) {
				byteBuffer.flip();
			}

			System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
			System.out.println("=============================================");
			System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

			// 聚集写入
			RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
			FileChannel channel2 = raf2.getChannel();

			channel2.write(bufs);

			// 关闭
			raf1.close();
			raf2.close();
			channel1.close();
			channel2.close();
		} catch (Exception e) {

		}
	}

	/*
	 * 利用通道完成文件的复制（非直接缓冲区）
	 */
	public static void test2() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");

			inChannel = fis.getChannel();
			outChannel = fos.getChannel();

			// 分配指定大小的缓冲区
			ByteBuffer buf = ByteBuffer.allocate(1024);

			// 将通道中的数据存入到缓冲区中
			while (inChannel.read(buf) != -1) {
				buf.flip(); // 切换读取数据的模式
				// 将缓冲区中的数据写入到通道中
				outChannel.write(buf);
				buf.clear();
			}
		} catch (Exception e) {

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inChannel != null) {
				try {
					inChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outChannel != null) {
				try {
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 使用直接缓冲区完成文件的复制(内存映射文件)
	 * 
	 * @throws IOException
	 */
	public static void test3() throws IOException {
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.READ, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE);

		// 内存映射文件
		MappedByteBuffer inMappedBuf = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappedBuf = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());

		// 直接对缓冲区进行数据的读写操作
		byte[] dst = new byte[inMappedBuf.limit()];

		inMappedBuf.get(dst);
		outMappedBuf.put(dst);

		inChannel.close();
		outChannel.close();
	}

	/**
	 * 通道之间的数据传输(直接缓冲区)
	 * 
	 * @throws IOException
	 */
	public static void test4() throws IOException {
		FileChannel inChannel = FileChannel.open(Paths.get("d:/1.mkv"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("d:/2.mkv"), StandardOpenOption.WRITE,
				StandardOpenOption.READ, StandardOpenOption.CREATE);

//			inChannel.transferTo(0, inChannel.size(), outChannel);
		outChannel.transferFrom(inChannel, 0, inChannel.size());

		inChannel.close();
		outChannel.close();
	}
}
