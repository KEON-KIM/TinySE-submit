package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.nio.ByteOrder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	static final int metasize = 12;
	static int blocksize;
	static int nblocks;
	
	private static RandomAccessFile tree;
	private static RandomAccessFile meta;
	
	Node cur;
	static int offset=1;
	List<Node> cache = new ArrayList<>();
	
	/*이부분은 tmp 파일 비우는 용도*/
	public static void clean(String dir) {
		File file = new File(dir);
		File[] tmpFiles = file.listFiles();
		if (tmpFiles != null) {
			for (int i = 0; i < tmpFiles.length; i++) {
				if (tmpFiles[i].isFile()) {
					tmpFiles[i].delete();
				} else {
					clean(tmpFiles[i].getAbsolutePath());
				}
				tmpFiles[i].delete();
			}
			file.delete();
		}
	}
	public static void init() {
		clean("./tmp");
		File treefile = new File("./tree.tree");
		File metafile = new File("./meta.meta");
		if(treefile.exists()) {
			treefile.delete();
		}
		if(metafile.exists()) {
			metafile.delete();
		}
	}
	public static String make_dir(String tmdir, int step) {
		String path = tmdir+File.separator+String.valueOf(step);
		File Folder = new File(path);
		
		if (!Folder.exists()) {
			try {
				Folder.mkdir();
			} catch(Exception e) {
				e.getStackTrace();
			}
		}
		return path;
	}
	
	public static void make_tmp(String tmdir) {
		File Folder = new File(tmdir);

		if (!Folder.exists()) {
			try {
				Folder.mkdir();
			} catch(Exception e) {
				e.getStackTrace();
			}
		}
	}

	public static  byte[] intTobyte(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}


	
	public static void writeToFile(RandomAccessFile file, int[] buffer, int position, int blocksize)
			throws IOException{
		file.seek(position*blocksize);
		byte[] bytebuffer = new byte[buffer.length*4];
		for(int i = 0; i < buffer.length; i++) {
			System.arraycopy(intTobyte(buffer[i]), 0, bytebuffer, i*4, 4);
		}
		file.write(bytebuffer);
	}
	
	/* size : stored node의 사이즈 */
	public static int[] readFromFile(RandomAccessFile file, int position, int blocksize, int node_size)
		throws IOException {
		byte[] bytebuffer = new byte[node_size*4];
		int[] buffer = new int[node_size];
		file.seek(position * blocksize);
		file.read(bytebuffer);
		
		ByteBuffer bf = ByteBuffer.wrap(bytebuffer);
		
		for(int i = 0; i < node_size; i++) {
			buffer[i] = bf.getInt();
		}
		return buffer;
	}
	
	/* meta file 양식
	 * status | offset | size */
	public static int[] readFromMeta(RandomAccessFile file, int position, int metasize)
		throws IOException {
		byte[] bytebuffer = new byte[metasize];
		int[] buffer = new int[3];
		file.seek(position*metasize);
		file.read(bytebuffer);
		
		ByteBuffer bf = ByteBuffer.wrap(bytebuffer);
		
		for(int i = 0; i < 3; i++) {
			buffer[i] = bf.getInt();
		}
		return buffer;
	}
	

	

	@Override
	public void close() throws IOException {
		this.tree.close();
		this.meta.close();
		
	}

	@Override
	public void insert(int key, int val) throws IOException{
		System.out.printf("insert %d, %d", key, val);
		System.out.println();
		try {
			this.get_root();
			this.search_leaf_node(key);
			this.cur.leaf_insert(key, val);
			if(!this.cur.isOver()) {
				this.writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
				this.writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
				System.out.print("offset : " + this.cur.offset +"  길이 " + this.cur.node_size +  " status : " + this.cur.status);
				System.out.print(" | 노드 : ");
				this.printarray(this.cur.to_tree_buffer());
				this.cur = null;
				this.cache = new ArrayList<>();
				return;
			}
			
			int[] piv = this.splitLeafNode();
			int last_cash = this.cache.size()-1;
			System.out.println(last_cash);
			while(last_cash > 1) {
				last_cash--;
				this.cur = this.cache.get(last_cash);
				this.cur.insert(piv[0], piv[1]);
				this.cache.remove(this.cache.size() - 1);
				if(!this.cur.isOver()) {
					this.writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
					this.writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
					System.out.print("offset : " + this.cur.offset +"  길이 " + this.cur.node_size +  " status : " + this.cur.status);
					System.out.print(" | 노드 : ");
					this.printarray(this.cur.to_tree_buffer());
					this.cur = null;
					this.cache = new ArrayList<>();
					
					this.get_root();
					return;
				}
				piv = this.splitNonLeafNode();
			}
			
			this.cur = cache.get(0);
			System.out.println(cur.keys);
			this.cur.insert(piv[0], piv[1]);
			System.out.println(cur.keys);
			if(!this.cur.isOver()) {
				this.writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
				this.writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
				
				this.writeToFile(this.tree, this.cur.to_tree_buffer(), 0, this.blocksize);
				this.writeToFile(this.meta, this.cur.to_meta_buffer(), 0, this.metasize);
				this.cur = null;
				this.cache = new ArrayList<>();
				return;
			}
			this.splitRootNode();
			this.cache = new ArrayList<>();
			this.cur = null;
			return;
			
			
			
			
		} catch(Exception e) {
			try {
				int[] meta_buffer = this.readFromMeta(this.meta, this.offset, this.metasize);
				int a = 1/meta_buffer[2];
				int[] tree_buffer = this.readFromFile(this.tree, this.offset, this.blocksize, meta_buffer[2]);
				
				this.cur = new Node(tree_buffer, this.blocksize, meta_buffer);
				this.cur.leaf_insert(key, val);
				
				if(this.cur.isOver()) {
					int[] piv = this.splitLeafNode();
					
					this.offset++;
					Node root = new Node(this.blocksize, 0, this.offset);
					
					root.vals.add(this.cur.offset);
					root.insert(piv[0], piv[1]);
					
					root.set_node_size();
					System.out.print("offset : " + root.offset +"  길이 " + root.node_size +  " status : " + root.status);
					System.out.print(" | 노드 : ");
					this.printarray(root.to_tree_buffer());
					
					this.writeToFile(this.tree, root.to_tree_buffer(), 0, this.blocksize);
					this.writeToFile(this.meta, root.to_meta_buffer(), 0, this.metasize);
					
					this.writeToFile(this.tree, root.to_tree_buffer(), root.offset, this.blocksize);
					this.writeToFile(this.meta, root.to_meta_buffer(), root.offset, this.metasize);
					
					return;
				}
				
				this.writeToFile(this.tree, this.cur.to_tree_buffer(), this.offset, this.blocksize);
				this.writeToFile(this.meta, this.cur.to_meta_buffer(), this.offset, this.metasize);
				return;
				
			}catch(Exception e2) {
				this.tree.seek(blocksize);
				this.tree.writeInt(val);
				this.tree.writeInt(key);
				
				this.meta.seek(metasize);
				this.meta.writeInt(2);
				this.meta.writeInt(this.offset);
				this.meta.writeInt(2);
				return;
			}
		}
	}
	public static void main(String[] args) throws IOException {
		init();
		String metapath = "./tmp/bplustree.meta";
		String savepath = "./tmp/bplustree.tree";
		String infile = "./src/test/resources/stage3-15000000.data";
		int nblocks = 32;
		int blocksize = 32;
		TinySEBPlusTree tree = new TinySEBPlusTree();
		tree.open(metapath, savepath, blocksize, nblocks);
		
		tree.insert(5, 10);
		tree.insert(6, 15);
		tree.insert(4, 20);
		tree.insert(7, 1);
		tree.insert(8, 5);
		tree.insert(17, 7);
		tree.insert(30, 8);
		tree.insert(1, 8);
		tree.insert(58, 1);
		tree.insert(25, 8);
		tree.insert(96, 32);
		tree.insert(21, 8);
//		tree.insert(9, 98);
//		tree.insert(57, 54);
//		tree.insert(157, 54);
//		tree.insert(247, 54);
//		tree.insert(357, 254);
//		tree.insert(557, 54);
		
		tree.close();
		
		tree = new TinySEBPlusTree();
		tree.open(metapath, savepath, blocksize, nblocks);
		
		System.out.println("key : 5 -> " + tree.search(5) +" | " + 10);
		System.out.println("key : 6 -> " + tree.search(6)+" | " +  15);
		System.out.println("key : 4 -> " + tree.search(4)+" | " +  20);
		System.out.println("key : 7 -> " + tree.search(7)+" | " +  1);
		System.out.println("key : 8 -> " + tree.search(8)+" | " +  5);
		System.out.println("key : 17-> " + tree.search(17)+" | " +  7);
		System.out.println("key : 30-> " + tree.search(30)+" | " +  8);
		System.out.println("key : 1 -> " + tree.search(1)+" | " +  8);
		System.out.println("key : 58-> " + tree.search(58)+" | " +  1);
		System.out.println("key : 25-> " + tree.search(25)+" | " +  8);
		System.out.println("key : 96-> " + tree.search(96)+" | " +  32);
		System.out.println("key : 21-> " + tree.search(21)+" | " +  8);
//		System.out.println("key : 9 -> " + tree.search(9)+" | " +  98);
//		System.out.println("key : 57-> " + tree.search(57)+" | " +  54);
//		System.out.println("key :157-> " + tree.search(157)+" | " +  54);
//		System.out.println("key :247-> " + tree.search(247)+" | " +  54);
//		System.out.println("key :357-> " + tree.search(357)+" | " +  254);
//		System.out.println("key :557-> " + tree.search(557)+" | " +  54);
	
		
		tree.close();
		
		
		
		
		
		
		
		
		
//		
//		DataInputStream is = new DataInputStream(
//								new BufferedInputStream(
//										new FileInputStream(infile), blocksize)
//										);
//
//		while(is.available() != 0) {
//			int key = is.readInt();
//			int val = is.readInt();
//			tree.insert(key, val);
//		}
//		
		
		/*read from file test*/
//		tree.tree.seek(0);
//		tree.tree.writeInt(3);
//		tree.tree.writeInt(4);
//		tree.tree.writeInt(5);
//		
//		int[] a = tree.readFromFile(tree.tree, 0, 1024, 3);
//		tree.printarray(a);
		
		
		/*split leaf test*/
//		int[] tree_buffer = new int[16];
//		for(int i = 0; i < 16; i ++) {
//			tree_buffer[i] = i;
//		}
//		int[] meta_buffer = new int[3];
//		meta_buffer[0] = 2;
//		meta_buffer[1] = 1;
//		meta_buffer[2] = 14;
//		tree.cur = new Node(tree_buffer, 64, meta_buffer);
//		System.out.println(tree.cur.keys);
//		System.out.println(tree.cur.vals);
//		tree.printarray(tree.cur.to_tree_buffer());
//		int[] piv = tree.splitLeafNode();
		
		/*get_root() test*/
//		tree.tree.seek(0);
//		tree.tree.writeInt(1);
//		tree.tree.writeInt(2);
//		tree.tree.writeInt(3);
//		
//		tree.meta.seek(0);
//		tree.meta.writeInt(5);
//		tree.meta.writeInt(6);
//		tree.meta.writeInt(3);
//		
//		tree.get_root();
//		System.out.println(tree.cur.keys);


	}
	//node = origin, root = new, leaf = new
	
	//node = Origin, newnode = new
	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
		String tmpdir = "./tmp/";
		make_tmp(tmpdir);
		this.tree = new RandomAccessFile(filepath, "rw");
		this.meta = new RandomAccessFile(metafile, "rw");
		this.blocksize = blocksize;
		this.nblocks = nblocks;
	}

	@Override
	public int search(int key) throws IOException {
		this.get_root();
		search_leaf_node(key);
		return this.cur.get_value(key);
		
	}

	public int[] splitLeafNode() throws IOException {
		int[] buffer;
		int[] piv = new int[2];
		int fanout = this.cur.max_num / 2 + 1;
		
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/

		buffer = this.cur.leaf_to_tree_buffer(fanout);
		
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, 2, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);
		piv[0] = brother.keys.get(1);
		piv[1] = brother.offset;
		
		System.out.print("offset : " + this.cur.offset + "  길이 " + this.cur.to_tree_buffer().length +  " status : " + this.cur.status);
		System.out.print(" | 노드 : ");
		this.printarray(this.cur.to_tree_buffer());
		System.out.print("offset : " + brother.offset + "  길이 " + brother.to_tree_buffer().length +  " status : " + brother.status);
		System.out.print(" | 노드 : ");
		this.printarray(brother.to_tree_buffer());
		
		return piv;
	}

	public int[] splitNonLeafNode() throws IOException{
		int[] buffer_;
		int[] piv = new int[2];
		int fanout = this.cur.max_num / 2 + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer_ = this.cur.to_tree_buffer(fanout);
		int[] buffer = new int[buffer_.length-1];
		System.arraycopy(buffer_, 1, buffer, 0, buffer.length);
		piv[0] = buffer_[0];
		piv[1] = this.offset + 1;
		
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, 1, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);
		
		System.out.println("원본 노드 길이 " + this.cur.to_tree_buffer().length + " offset : " + this.cur.offset + " status : " + this.cur.status);
		System.out.println("형제 노드 길이 " + brother.to_tree_buffer().length + " offset : " + brother.offset + " status : " + brother.status);
		System.out.print("형제노드 node size : " + brother.node_size + " node : ");
		this.printarray(brother.to_tree_buffer());
		System.out.println("형제노드 get val : " + brother.get_point(9));

		return piv;
	}
	
	
	public void splitRootNode() throws IOException {
		this.cur.status = 1;
		int[] buffer_;
		int[] piv = new int[2];
		int fanout = this.cur.max_num / 2 + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		
		buffer_ = this.cur.to_tree_buffer(fanout);
		int[] buffer = new int[buffer_.length-1];
		System.arraycopy(buffer_, 1, buffer, 0, buffer.length);
		piv[0] = buffer_[0];
		piv[1] = this.offset + 1;
		
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
		
		this.offset++;
		Node brother = new Node(buffer, this.blocksize, 1, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);
		
//		System.out.println("원본 노드 길이 " + this.cur.to_tree_buffer().length + " offset : " + this.cur.offset + " status : " + this.cur.status);
//		System.out.println("형제 노드 길이 " + brother.to_tree_buffer().length + " offset : " + brother.offset + " status : " + brother.status);
//		System.out.print("형제노드 node size : " + brother.node_size + " node : ");
//		this.printarray(brother.to_tree_buffer());
		
		this.offset++;
		Node root = new Node(this.blocksize, 0, this.offset);
		root.vals.add(this.cur.offset);
		root.insert(piv[0], piv[1]);
		root.set_node_size();
		
		writeToFile(this.tree, root.to_tree_buffer(), root.offset, this.blocksize);
		writeToFile(this.meta, root.to_meta_buffer(), root.offset, this.metasize);
		writeToFile(this.tree, root.to_tree_buffer(), 0, this.blocksize);
		writeToFile(this.meta, root.to_meta_buffer(), 0, this.metasize);
		
//		System.out.println("루트 노드 길이 " + root.to_tree_buffer().length + " offset : " + root.offset + " status : " + root.status);
//		System.out.print("루트 노드 node size : " + root.node_size + " node : ");
//		this.printarray(root.to_tree_buffer());
		
	}

	//status, offset 순으로 저장함
	
	//leaf 찾기

	public void search_leaf_node(int key) throws IOException {
		if(this.cur.status == 2) {
			return;
		}
		get_child(key);
		search_leaf_node(key);
	}
	public void get_child(int key) throws IOException {
		if(this.cur.status == 2) {
//			System.out.println("this is leaf node");
			return ;
		}
		
		int point = this.cur.get_point(key);
		int[] meta_buffer = readFromMeta(this.meta, point, this.metasize);
		int[] tree_buffer = readFromFile(this.tree, point, this.blocksize, meta_buffer[2]);
		
		this.cur = new Node(tree_buffer, this.blocksize, meta_buffer);
		this.cache.add(new Node(tree_buffer, this.blocksize, meta_buffer));
	}
	public void get_root() throws IOException {
		
			int[] meta_buffer = readFromMeta(this.meta, 0, this.metasize);
			int[] tree_buffer = readFromFile(this.tree, 0, this.blocksize, meta_buffer[2]);
			int a = 1 / tree_buffer[1];
			
			
			this.cur = new Node(tree_buffer, this.blocksize, meta_buffer);
			this.cache.add(new Node(tree_buffer, this.blocksize, meta_buffer));
		
	}

	public static void printarray(int[] array) {
		for(int i : array) {
			System.out.print(i+", ");
		}
		System.out.println();
	}



}

class Node {
	/* status 
	 * 0 : root
	 * 1 : non leaf
	 * 2 : leaf
	 */
	int offset;
	int status; 
	int node_size;
	int max_num;
	
	List<Integer> vals;
	List<Integer> keys;
	
	
	/* node size를 계속 추적할지
	 * 마지막에한번 할지 결정해야함*/
	/* file에서 가져와서 노드 생성할때*/
	Node(int[] tree_buffer, int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
		
		for(int i = 0; i < this.max_num; i++) {
			try {
				this.vals.add(tree_buffer[i*2]);
				this.keys.add(tree_buffer[i*2 + 1]);
			} catch(Exception e) {break;}
		}
	}
	
	/* 새로운 노드를 생성할 때*/
	Node(int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
	}
	
	Node(int[] tree_buffer, int blocksize, int[] meta_buffer) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = meta_buffer[0];
		this.offset = meta_buffer[1];
		
		for(int i = 0; i < this.max_num; i++) {
			try {
				this.vals.add(tree_buffer[i*2]);
				this.keys.add(tree_buffer[i*2 + 1]);
			} catch(Exception e) {break;}
		}	
		
	}
	
	/* file을 쓰고 읽을때 꼭 필요함.*/
	public void set_node_size() {
		this.node_size = (this.keys.size() - 1 + this.vals.size());
	}
	public boolean isOver() {
		if(keys.size()-1 >= this.max_num) return true;
		return false;
	}
	

	/*
	 * insert를 하기전에 full인지 아닌지부터 check*/
	public void leaf_insert(int key, int val) {
		try {
			int tmp = this.keys.get(1); // 예외가 뜨면 keys는 empty
			Iterator<Integer> it = this.keys.iterator();
			tmp = it.next(); // 첫 value(-1)은 버리는용도
			while(it.hasNext()) {
				int n = it.next();
				if(n > key) {
					int index = this.keys.indexOf(n);
					this.vals.add(index-1, val);
					this.keys.add(index, key);
					return;
				}
			}
			this.vals.add(val);
			this.keys.add(key);
		}catch(Exception e) {
			this.keys.add(key);
			this.vals.add(val);
		}
		
	}
	public void insert(int key, int val) {
		try {
			int tmp = this.keys.get(1); // 예외가 뜨면 keys는 empty
			Iterator<Integer> it = this.keys.iterator();
			tmp = it.next(); // 첫 value(-1)은 버리는용도
			while(it.hasNext()) {
				int n = it.next();
				if(n > key) {
					int index = this.keys.indexOf(n);
					this.vals.add(index, val);
					this.keys.add(index, key);
					return;
				}
			}
			this.vals.add(val);
			this.keys.add(key);
			
		} catch(Exception e) {
			this.keys.add(key);
			this.vals.add(val);
		}
	}
	
	
	/*
	 * keys, vals를 int 배열 buffer로 만들기*/
	public int[] to_tree_buffer() {
		this.set_node_size();
		
		int[] tree_buffer = new int[this.node_size];
		
		for(int i = 0; i < this.max_num+1; i++) {
			try {
				tree_buffer[i*2] = this.vals.get(i);
				tree_buffer[i*2 + 1] = this.keys.get(i+1);
			} catch(Exception e) {
				break;
			}
		}
		return tree_buffer;
	}
	
	public int[] leaf_to_tree_buffer(int index) {
		this.node_size = 2*(index - 1);
		
		
		int num_buffer = this.keys.size() + this.vals.size() - this.node_size - 1;
		int[] buffer = new int[num_buffer];
		
		for(int i = 0; i < num_buffer/2 ; i++) {
			buffer[i*2] = this.vals.get(index-1);
			this.vals.remove(index-1);
			buffer[i*2+1] = this.keys.get(index);
			this.keys.remove(index);
		}
		return buffer;
	}
	public int[] to_tree_buffer(int index) {
		this.node_size = 2*(index - 1);
		
		int num_buffer = this.keys.size() + this.vals.size() - this.node_size - 1;
		int[] buffer = new int[num_buffer];
		for(int i = 0; i < num_buffer / 2 ; i++) {
			buffer[i*2] = this.keys.get(index);
			this.keys.remove(index);
			buffer[i*2+1] = this.vals.get(index);
			this.vals.remove(index);
		}
		return buffer;
	}
	public int[] to_meta_buffer() {
		int[] buffer = new int[3];
		buffer[0] = this.status;
		buffer[1] = this.offset;
		buffer[2] = this.node_size;
		
		return buffer;
	}
	/* index부터 keys, vals remove*/
	
	public int get_point(int key){ 
		Iterator<Integer> it = this.keys.iterator();
		int tmp = it.next();
		while(it.hasNext()) {
			int n = it.next();
			if(n > key) {
				return this.vals.get(this.keys.indexOf(n)-1);
			}
		}
		
		return this.vals.get(vals.size() - 1);
	}
	
	public int get_value(int key) {
		int a =  this.keys.indexOf(key);
		if(a == -1) return a;
		
		return this.vals.get(a-1);
	}
	
}




