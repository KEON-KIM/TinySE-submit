package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	final static int metasize = 12;
	static int blocksize;
	static int nblocks;
	static int fanout;
	int meta_zero;
	int meta_one;
	int meta_two;
	int height = 1;
	
	private RandomAccessFile tree;
	private RandomAccessFile meta;
	
	Node cur;
	Node root;
	int offset=1;
	List<Node> cache = new ArrayList<>(TinySEBPlusTree.nblocks);

	int[] piv = new int[2];
	byte[] tree_buffer;
	byte[] meta_buffer;
	public void printoffset() {
		System.out.println("offset : " + this.offset);
	}
	public void printheight() {
		System.out.println("height : " + this.height);
	}
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


	public void writeRootTree(byte[] tree_buffer)
			throws IOException{
		this.tree.seek(0);
		this.tree.write(tree_buffer);
	}
	
	
	public void writeRootMeta(byte[] meta_buffer)
			throws IOException{
		this.meta.seek(0);
		this.meta.write(meta_buffer);
	}
	
	public void writeToTree(byte[] tree_buffer)
			throws IOException{
		this.tree.seek(this.cur.offset*TinySEBPlusTree.blocksize);
		this.tree.write(tree_buffer);
	}
	public void writeToMeta(byte[] meta_buffer)
			throws IOException{
		this.meta.seek(this.cur.offset*metasize);
		this.meta.write(meta_buffer);
	}
	
	/* size : stored node의 사이즈 */
	public byte[] readFromTree(int position, int node_size)
		throws IOException {
		byte[] tree_buffer = new byte[node_size*4];
		this.tree.seek(position * TinySEBPlusTree.blocksize);
		this.tree.read(tree_buffer);
		return tree_buffer;
	}
	
	/* meta file 양식
	 * status | offset | size */
	public byte[] readFromMeta(int position)
		throws IOException {
		byte[] meta_buffer = new byte[metasize];
		this.meta.seek(position*metasize);
		this.meta.read(meta_buffer);
		return meta_buffer;
	}
	
	public void write() throws IOException {
		this.writeToTree(this.cur.to_tree_buffer());
		this.writeToMeta(this.cur.to_meta_buffer());
	}

	

	@Override
	public void close() throws IOException {
		
		this.tree.close();
		this.meta.close();
		this.root = null;
		this.cur = null;
		
		
	}
	public void printallnode() throws IOException {
		
		System.out.println("currente offset : " + this.offset);
		for(int i = 0; i <= this.offset; i++) {
			byte[] meta_buffer = readFromMeta(i);
			byte[] tree_buffer = readFromTree(i, meta_buffer[11]);
			Node node;
			if(meta_buffer[3] == 2) {
				node = new leafNode(tree_buffer, TinySEBPlusTree.blocksize, meta_buffer);
				node.set_node_size();
				System.out.printf("offset(%d) node(%d) : ", i, node.node_size);
				printarray(node.to_tree_buffer());
			} else {
				node = new nonleafNode(tree_buffer, TinySEBPlusTree.blocksize, meta_buffer);
				node.set_node_size();
				System.out.printf("offset(%d) node(%d) : ", i, node.node_size);
				printarray(node.to_tree_buffer());
			}
			
		}
	}
	public int meta(int offset, int i) throws IOException {
		this.meta_buffer = this.readFromMeta(offset);
		ByteBuffer bf = ByteBuffer.wrap(meta_buffer);
		int[] tmp = new int[3];
		tmp[0] = bf.getInt();
		tmp[1] = bf.getInt();
		tmp[2] = bf.getInt();
		this.meta_buffer = null;
		return tmp[i];
	}

	@Override
	public void insert(int key, int val) throws IOException{

//		System.out.printf("====insert %d, %d===", key, val);
//		System.out.println();
		if(this.root != null) {
			this.cache.add(this.root);
			
			this.search_leaf_node(key);
			
			this.cur.insert(key, val);
			if(!this.cur.isOver()) {
				this.write();
				this.cur = null;
				this.cache = new ArrayList<>();
//				this.printallnode();

				return;
			}

			this.splitLeafNode();
			int cash_index = this.cache.size()-1;

			
			while(cash_index > 1) {
				cash_index--;
				this.cur = this.cache.get(cash_index);
				this.cur.insert(this.piv[0], this.piv[1]);
				if(!this.cur.isOver()) {
					this.write();
					this.cur = null;
					this.cache = new ArrayList<>();
//					this.printallnode();

					return;
				}
				this.splitNonLeafNode();
			}
			
			//root
			this.cur = cache.get(0);
			this.cur.insert(this.piv[0], this.piv[1]);
			if(!this.cur.isOver()) {
				this.write();
				this.writeRootTree(this.cur.to_tree_buffer());
				this.writeRootMeta(this.cur.to_meta_buffer());
				
				this.cur = null;
				this.cache = new ArrayList<>();
//				this.printallnode();

				return;
			}
//			this.height++;
			this.splitRootNode();
			this.cache = new ArrayList<>();
			this.cur = null;
//			this.printallnode();

			return;
		} else {
			this.cur.insert(key, val);
			if(!this.cur.isOver()) {
				this.write();
				
//				System.out.printf("offset(%d) node(%s) : ", this.cur.offset, this.cur.node_size);
//				this.printarray(this.cur.leaf_to_tree_buffer());

				return;
			}
			this.splitLeafNode();
			
			this.offset++;
			this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset); //non leaf 
			this.cur.vals.add(this.offset - 2);
			this.cur.insert(this.piv[0], this.piv[1]);
//			this.height++;
			this.root = this.cur.copyNode();
			
			this.write();
			
//			this.printallnode();
			this.cur = null;

			return;
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
		int adv = 10;
		TinySEBPlusTree.blocksize = blocksize/adv;
		TinySEBPlusTree.nblocks = nblocks;
		int num_keys = ((blocksize / adv) / Integer.BYTES) / 2;
		TinySEBPlusTree.fanout = num_keys * 1 / 2 + 1;
		
		
		if(this.tree.length() != 0) {
			this.meta_buffer = readFromMeta(0);
			ByteBuffer bf = ByteBuffer.wrap(this.meta_buffer);
			this.meta_zero = bf.getInt();
			this.meta_one = bf.getInt();
			this.meta_two = bf.getInt();
			this.tree_buffer = readFromTree(0, this.meta_two);
			this.root = new nonleafNode(tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer);
			this.meta_buffer = null;
			this.tree_buffer = null;
		} else {
			this.cur = new leafNode(TinySEBPlusTree.blocksize, 2, this.offset);
		}
	}


	public void splitLeafNode() throws IOException {
//		this.fanout = this.cur.max_num / 2 + 1;
		
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		this.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
		this.write();
		
		
		this.offset++;
		this.cur = new leafNode(this.tree_buffer, TinySEBPlusTree.blocksize, 2, this.offset); //leaf node
		this.write();
		this.piv[0] = this.cur.keys.get(1);
		this.piv[1] = this.cur.offset;
		this.tree_buffer = null;
	}

	public void splitNonLeafNode() throws IOException{
		
//		this.fanout = this.cur.max_num / 2 + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		this.piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
		this.piv[1] = this.offset + 1;
		
		this.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);

		this.write();
		
		this.offset++;
		this.cur = new nonleafNode(this.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);

		this.write();
		this.tree_buffer = null;
	}
	
	
	public void splitRootNode() throws IOException {
		this.cur.status = 1;
		
//		this.fanout = this.cur.max_num / 2 + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		this.piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
		this.piv[1] = this.offset + 1;
		
		this.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
		
		this.write();
		
		int cur_offset = this.cur.offset;
		this.offset++;
		this.cur = new nonleafNode(this.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);
		this.write();
		
		this.offset++;
		this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset);
		this.cur.vals.add(cur_offset);
		this.cur.insert(this.piv[0], this.piv[1]);
		this.root = this.cur.copyNode();

		this.write();
		this.writeRootTree(this.cur.to_tree_buffer());
		this.writeRootMeta(this.cur.to_meta_buffer());
		this.tree_buffer = null;
		
	}

	//status, offset 순으로 저장함
	@Override
	public int search(int key) throws IOException {
		this.cur = this.root;
		search_leaf_node_(key);
		int result = this.cur.get_value(key);
		this.cur = null;
		return result;
		
	}

	//leaf 찾기
	public void search_leaf_node_(int key) throws IOException {
		while(this.cur.status != 2) {
			this.get_child_(key);
		}
	}
	public void get_child_(int key) throws IOException {
		int point = this.cur.get_value(key);
		this.meta_buffer = readFromMeta(point);
		ByteBuffer bf = ByteBuffer.wrap(this.meta_buffer);
		this.meta_zero = bf.getInt();
		this.meta_one = bf.getInt();
		this.meta_two = bf.getInt();
		this.tree_buffer = readFromTree(point, this.meta_two);
		if (this.meta_zero == 1) {
			this.cur = new nonleafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer);
		} else {
			this.cur = new leafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer);
		}
		this.meta_buffer = null;
		this.tree_buffer = null;
		
	}
	public void search_leaf_node(int key) throws IOException {
		this.cur = this.root;
		while(this.cur.status != 2) {
			get_child(key);
		}
	}
	public void get_child(int key) throws IOException {
		int point = this.cur.get_value(key);
		this.meta_buffer = readFromMeta(point);
		ByteBuffer bf = ByteBuffer.wrap(this.meta_buffer);
		this.meta_zero = bf.getInt();
		this.meta_one = bf.getInt();
		this.meta_two = bf.getInt();
		this.tree_buffer = readFromTree(point, this.meta_two);
		if (this.meta_zero == 1) {
			this.cur = new nonleafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer);
			this.cache.add(new nonleafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer));
			return;
		}
		this.cur = new leafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer);
		this.cache.add(new leafNode(this.tree_buffer, TinySEBPlusTree.blocksize, this.meta_buffer));
		
		
	}

	public static void printarray(byte[] array) {
		ByteBuffer bf = ByteBuffer.wrap(array);
		for(int i=0; i < array.length/4; i++) {
			System.out.print(bf.getInt()+", ");
		}
		System.out.println();
	}
	public void printarray() {
		
		for(int i : this.cur.to_tree_buffer()) {
			System.out.print(i+", ");
		}
		System.out.println();
	}
	public static void main(String[] args) throws IOException {
		//mvn clean -D test=BPlusTreeTest#bPlusTreeTestWithLargeFile test
		//mvn clean -D test=BPlusTreeTest#bPlusTreeTest test
		init();
		String metapath = "./tmp/bplustree.meta";
		String savepath = "./tmp/bplustree.tree";
//		String testfile = "/home/hms/Desktop/TinySE-submit/src/test/resources/stage3-500000.data";
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
		tree.insert(9, 98);
		tree.insert(57, 54);
		tree.insert(157, 54);
		tree.insert(247, 54);
		tree.insert(357, 254);
		tree.insert(557, 54);
		tree.insert(600, 23);
		
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
		System.out.println("key : 9 -> " + tree.search(9)+" | " +  98);
		System.out.println("key : 57-> " + tree.search(57)+" | " +  54);
		System.out.println("key :157-> " + tree.search(157)+" | " +  54);
		System.out.println("key :247-> " + tree.search(247)+" | " +  54);
		System.out.println("key :357-> " + tree.search(357)+" | " +  254);
		System.out.println("key :557-> " + tree.search(557)+" | " +  54);
		System.out.println("key :558-> " + tree.search(600)+" | " +  23);
	
		
		tree.close();

	}
}

