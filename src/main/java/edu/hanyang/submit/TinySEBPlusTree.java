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
	public static void main(String[] args) throws IOException {
		init();
		String metafile = "./tmp/meta.meta";
		String filepath = "./tmp/tree.tree";
		String infile = "./src/test/resources/stage3-15000000.data";
		int nblocks = 2000;
		int blocksize = 4096;
		TinySEBPlusTree tree = new TinySEBPlusTree();
		tree.open(metafile, filepath, blocksize, nblocks);
		DataInputStream is = new DataInputStream(
								new BufferedInputStream(
										new FileInputStream(infile), blocksize)
										);
		
		
		RandomAccessFile file = new RandomAccessFile(filepath, "rw");
		
		
		int[] buffer = new int[10];
		for(int i = 120; i < 130; i++) {
			buffer[i-120] = i;
		}
		tree.printarray(buffer);
		tree.writeToFile(tree.tree, buffer, 0, 1024);
		
		
		int[] test = tree.readFromFile(file, 0, 1024, 10);
		tree.printarray(test);
		
	}
	public static  byte[] intTobyte(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte)(value >> 24);
		byteArray[1] = (byte)(value >> 16);
		byteArray[2] = (byte)(value >> 8);
		byteArray[3] = (byte)(value);
		return byteArray;
	}


	
	private static void writeToFile(RandomAccessFile file, int[] buffer, int position, int blocksize)
			throws IOException{
		file.seek(position*blocksize);
		byte[] bytebuffer = new byte[buffer.length*4];
		for(int i = 0; i < buffer.length; i++) {
			System.arraycopy(intTobyte(buffer[i]), 0, bytebuffer, i*4, 4);
		}
		file.write(bytebuffer);
	}
	
	/* size : stored node의 사이즈 */
	private static int[] readFromFile(RandomAccessFile file, int position, int blocksize, int node_size)
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
	private static int[] readFromMeta(RandomAccessFile file, int position, int blocksize)
		throws IOException {
		file.seek(position*blocksize);
		int[] integers = new int[3];
		for(int i = 0; i < integers.length; i++) {
			integers[i] = file.readInt();
		}
		return integers;
	}
	

	

	@Override
	public void close() throws IOException {
		this.tree.close();
		this.meta.close();
		
	}

	@Override
	public void insert(int key, int val) throws IOException{
		try {
			this.get_root();
			this.search_leaf_node(key); // leaf node
			System.out.println("leaf node get");
			this.cur.insert(key, val);
			if(!this.cur.isOver()) {
			//	System.out.println("max_vals" + (this.cur.max_vals + this.cur.max_keys));
				System.out.println(cur.offset + " cur.size : " + this.cur.to_tree_buffer().length);
				System.out.println("leaf node 안차서 node안에 삽입");
				writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
				this.cur = null;
				this.cache = null;
				this.cache = new ArrayList<>(); //cache 초기화
				return ;
			}
			/* leaf 노드에서 piv 뽑고 마지막 cache node remove*/
			int[] piv = this.splitLeafNode();
			System.out.println("leaf 꽉차서 leaf split");
			this.cache.remove(this.cache.size() - 1);
			System.out.println("cache에서 leaf 지우기");
			int last_level = this.cache.size()-1;
			
			while(last_level > 1) {
				cur = cache.get(last_level);
				System.out.println("부모 노드로 옴");
				this.cur.insert(piv[0], piv[1]);
				if(!this.cur.isOver()) {
					System.out.println("부모노드 꽉안차서 그대로 삽입");
					writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
					this.cur = null;
					this.cache = null;
					this.cache = new ArrayList<>(); //cache 초기화
					return ;
				}
				System.out.println("부모노드 꽉차서 부모노드 split");
				piv = this.splitNonLeafNode();
				this.cache.remove(this.cache.size() - 1);
				last_level--;
			}
			
			/*root node 단계*/
			cur = cache.get(last_level);
			System.out.println("root로 옴");
			if(!this.cur.isOver()) {
				System.out.println("root 안차서 insert");
				this.cur.insert(piv[0], piv[1]);
				writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
				this.cur = null;
	//			this.cache = null;
				this.cache = new ArrayList<>(); //cache 초기화
				return ;
			}
			System.out.println("root 꽉차서 root split");
			this.splitRootNode();
			this.cache = null;
			this.cur = null;
			return ;
			
			
		} catch(IOException e) {
			try {
				int[] tree_buffer = this.readFromFile(tree, 1, this.blocksize);
				int[] meta_buffer = this.readFromMeta(meta, 1, this.metasize);
				
				cur = new Node(tree_buffer, this.blocksize, meta_buffer);
				cur.insert(key, val);
				if(cur.keys.size() == cur.max_keys) {
					System.out.println("첫 split 일어남");
					int[] piv = this.splitLeafNode();
					this.offset++;
					Node root = new Node(this.blocksize, 0, this.offset);
					root.vals.add(piv[1]);
					root.insert(piv[0], cur.offset);
					this.writeToFile(tree, root.to_tree_buffer(), 0, this.blocksize);
					this.writeToFile(meta, root.to_meta_buffer(), 0, this.metasize);
					this.cur = null;
					return ;
				}
				System.out.println("꽉안차서 그냥 삽입");
				this.writeToFile(tree, cur.to_tree_buffer(), cur.offset, this.blocksize);
				this.writeToFile(meta, cur.to_meta_buffer(), cur.offset, this.blocksize);
				return ;
				
			} catch(IOException e2) {
				tree.seek(this.blocksize);
				tree.writeInt(val);
				tree.writeInt(key);
				meta.writeInt(2);
				meta.writeInt(this.offset);
				
				return;
			}
		}
		
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
	
	public static boolean isLeafNode(Node node) {
		if(node.status == 2||node.status==3)
			return true;
		return false;
	}
	public static boolean isRootNode(Node node) {
		if(node.status == 0||node.status==3)
			return true;
		return false;
	}
	
	public int[] splitLeafNode() throws IOException {
		int[] buffer;
		int[] piv = new int[2];
		int fanout = (this.cur.max_vals / 2) + 1;
		
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer = this.cur.to_tree_buffer(fanout);
		this.cur.remove(fanout);
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, 2, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);
		piv[0] = brother.keys.get(0);
		piv[1] = this.offset;
		
		System.out.println("원본 노드 길이 " + this.cur.to_tree_buffer().length + " offset : " + this.cur.offset + " status : " + this.cur.status);
		System.out.println("형제 노드 길이 " + brother.to_tree_buffer().length + " offset : " + brother.offset + " status : " + brother.status);
		return piv;
	}
	
	public int[] splitNonLeafNode() throws IOException{
		int[] buffer;
		int[] piv = new int[2];
		int fanout = (this.cur.max_vals / 2) + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer = this.cur.to_tree_buffer(fanout+1);
		piv[0] = this.cur.keys.get(fanout);
		piv[1] = this.cur.vals.get(fanout);
		this.cur.remove(fanout);
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, 1, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);

		return piv;
	}
	public void splitRootNode() throws IOException {
		this.cur.status = 1;
		int[] buffer;
		int[] piv = new int[2];
		int fanout = (this.cur.max_vals / 2) + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer = this.cur.to_tree_buffer(fanout+1);
		piv[0] = this.cur.keys.get(fanout);
		piv[1] = this.cur.vals.get(fanout);
		this.cur.remove(fanout);
		writeToFile(this.tree, this.cur.to_tree_buffer(), this.cur.offset, this.blocksize);
		writeToFile(this.meta, this.cur.to_meta_buffer(), this.cur.offset, this.metasize);
		
		this.offset++;
		Node brother = new Node(buffer, this.blocksize, 1, this.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, this.metasize);
		
		this.offset++;
		Node root = new Node(this.blocksize, 0, this.offset);
		root.vals.add(piv[1]);
		root.insert(piv[0], this.cur.offset);
		System.out.println("root offset when new :" + root.offset);
		writeToFile(this.tree, brother.to_tree_buffer(), 0, this.blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), 0, this.metasize);
		
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
			return ;
		}
		
		int point = this.cur.get_point(key);
		int[] tree_buffer = readFromFile(this.tree, point, this.blocksize);
		int[] meta_buffer = readFromMeta(this.meta, point, this.metasize);
		this.cur = new Node(tree_buffer, this.blocksize, meta_buffer);
		this.cache.add(new Node(tree_buffer, this.blocksize, meta_buffer));
	}
	public void get_root() throws IOException {
		try {
			int[] tree_buffer = readFromFile(this.tree, 0, this.blocksize);
			if(tree_buffer[0] == 0) throw new Exception();
			int[] meta_buffer = readFromMeta(this.meta, 0, this.metasize);
			this.cur = new Node(tree_buffer, this.blocksize, meta_buffer);
			this.cache.add(new Node(tree_buffer, this.blocksize, meta_buffer));
		} catch(Exception e) {
			System.out.println("no root");
			throw new IOException();
		}
		
		
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
		if(keys.size()-1 > this.max_num) return true;
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
		
		for(int i = 0; i < this.max_num; i++) {
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
		this.node_size = 2*index - 2;
		
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
		this.node_size = 2*index - 1;
		
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
		int[] buffer = new int[2];
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





