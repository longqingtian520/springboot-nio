package com.criss.wang;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootNioApplicationTests {

	@Test
	public void contextLoads() {
		
		String str = "abcde";
		
		// 1、分配一个指定大小的缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		System.out.println("-----------------allocate()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 2、利用put()存入数据到缓冲区中
		buf.put(str.getBytes());
		
		System.out.println("-----------------put()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 3、切换读取数据模式
		buf.flip();

		System.out.println("-----------------flip()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 4、利用get()方法读取数据
		byte[] dst = new byte[buf.limit()];
		buf.get(dst);
		System.out.println(new String(dst, 0, dst.length));
		
		System.out.println("-----------------get()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 5、rewind() ： 可重复读
		buf.rewind();

		System.out.println("-----------------rewind()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
		
		// 6、clear() : 清空缓冲区
		buf.clear();
		
		System.out.println("-----------------clear()-------------------");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
	}

}
