package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	//-XX:+HeapDumpOnOutOfMemoryError
	final static int metasize = 12;
	static int blocksize;
	static int nblocks;
	static int fanout;
	
	private static RandomAccessFile tree;
	private static RandomAccessFile meta;
	
	Node cur;
	Node root;
	int offset=1;
	Stack<Node> pedigree = new Stack<>();

	static int[] piv;
	static ByteBuffer tree_buffer;
	static ByteBuffer meta_buffer;
	
	public void printoffset() {
		System.out.println("offset : " + this.offset);
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
	public static void writeFile(Node node)
			throws IOException{
		byte[] tree_buffer = node.to_tree_buffer().array();
		byte[] meta_buffer = node.to_meta_buffer().array();
		int offset = node.offset;
		
		TinySEBPlusTree.tree.seek(offset*TinySEBPlusTree.blocksize);
		TinySEBPlusTree.tree.write(tree_buffer);
		TinySEBPlusTree.meta.seek(offset*TinySEBPlusTree.metasize);
		TinySEBPlusTree.meta.write(meta_buffer);
		
		if(node.status == 0) {
			TinySEBPlusTree.tree.seek(0);
			TinySEBPlusTree.tree.write(tree_buffer);
			TinySEBPlusTree.meta.seek(0);
			TinySEBPlusTree.meta.write(meta_buffer);
		}
	}
	
	/* size : stored node의 사이즈 */
	public static Node readFile(int position) throws IOException {
		byte[] buffer = new byte[TinySEBPlusTree.metasize];
		TinySEBPlusTree.meta.seek(position*TinySEBPlusTree.metasize);
		TinySEBPlusTree.meta.read(buffer);
		ByteBuffer bf = ByteBuffer.wrap(buffer);
		int meta_zero = bf.getInt();
		int meta_one = bf.getInt();
		int meta_two = bf.getInt();
		bf.clear();
		buffer = new byte[meta_two*4];
		TinySEBPlusTree.tree.seek(position * TinySEBPlusTree.blocksize);
		TinySEBPlusTree.tree.read(buffer);
		bf = ByteBuffer.wrap(buffer);
		if(meta_zero == 2) {
			return new leafNode(bf, TinySEBPlusTree.blocksize, meta_zero, meta_one);
		}else {
			return new nonleafNode(bf, TinySEBPlusTree.blocksize, meta_zero, meta_one);
		}
	}
	

	

	

	@Override
	public void close() throws IOException {
		pedigree.clear();
		TinySEBPlusTree.tree.close();
		TinySEBPlusTree.meta.close();
		this.root = null;
		this.cur = null;
		
		
	}
	public void printallnode() throws IOException {
		System.out.println("currente offset : " + this.offset);
		for(int i = 0; i <= this.offset; i++) {
			Node node;
			node = TinySEBPlusTree.readFile(i);
			node.set_node_size();
			System.out.printf("offset(%d) node(%d) : ", i, node.node_size);
			printarray(node.to_tree_buffer().array());
			
		}
	}

	@Override
	public void insert(int key, int val) throws IOException{
//		System.out.printf("====insert %d, %d===", key, val);
//		System.out.println();
		if(this.root != null) {
			this.pedigree.push(this.root);
			this.search_leaf_node(key);
			this.cur.insert(key, val);
			if(!this.cur.isOver()) {
//				cache.put(this.cur.offset, this.cur);
				writeFile(this.cur);
				this.pedigree.clear();
				this.cur.keys.clear();
				this.cur.vals.clear();
//				this.printallnode();
				return;
			}
			this.splitLeafNode();
			this.pedigree.pop();
			while(this.pedigree.size() > 1) {
				this.cur = this.pedigree.pop();
				this.cur.insert(piv[0], piv[1]);
				if(!this.cur.isOver()) {
//					cache.put(this.cur.offset, this.cur);
					writeFile(this.cur);
					this.pedigree.clear();
					this.cur.keys.clear();
					this.cur.vals.clear();
//					this.printallnode();
					return;
				}
				this.splitNonLeafNode();
			}
			//root
			this.cur = this.pedigree.pop();
			this.cur.insert(piv[0], piv[1]);
			if(!this.cur.isOver()) {
//				cache.put(this.cur.offset, this.cur);
				writeFile(this.cur);
				this.pedigree.clear();
	//			this.printallnode();
				return;
			}
			this.splitRootNode();
			this.pedigree.clear();
//			this.printallnode();
			return;
		} else {
			this.cur.insert(key, val);
			if(!this.cur.isOver()) {
//				cache.put(this.cur.offset, this.cur);
				writeFile(this.cur);
				
//				System.out.printf("offset(%d) node(%s) : ", this.cur.offset, this.cur.node_size);
//				this.printarray(this.cur.leaf_to_tree_buffer());
				return;
			}
			this.splitLeafNode();
			this.offset++;
			this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset); //non leaf 
			this.cur.vals.add(this.offset - 2);
			this.cur.insert(piv[0], piv[1]);
			this.root = this.cur.copyNode();		
//			cache.put(this.cur.offset, this.cur);
			writeFile(this.cur);
			this.cur.keys.clear();
			this.cur.vals.clear();
//			this.printallnode();
			return;
		}
		
	}

	//node = origin, root = new, leaf = new
	
	//node = Origin, newnode = new
	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
		String tmpdir = "./tmp/";
		make_tmp(tmpdir);
		TinySEBPlusTree.tree = new RandomAccessFile(filepath, "rw");
		TinySEBPlusTree.meta = new RandomAccessFile(metafile, "rw");
		int adv;
		if(blocksize > 32) {
			adv = 10;
		} else {
			adv = 1;
		}
		TinySEBPlusTree.blocksize = blocksize/adv;
		TinySEBPlusTree.nblocks = nblocks;
		/*fanout setting*/
		int num_keys = (TinySEBPlusTree.blocksize / Integer.BYTES) / 2;
		TinySEBPlusTree.fanout = num_keys * 1 / 2 + 1;
		piv = new int[2];
		if(TinySEBPlusTree.tree.length() != 0) {
			this.root = TinySEBPlusTree.readFile(0);
		} else {
			this.cur = new leafNode(TinySEBPlusTree.blocksize, 2, this.offset);
		}
	}


	public void splitLeafNode() throws IOException {
//		this.fanout = this.cur.max_num / 2 + 1;
		
		/* cur node 작업 */
		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		
		this.offset++;
		this.cur = new leafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 2, this.offset); //leaf node
		TinySEBPlusTree.tree_buffer.clear();
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		piv[0] = this.cur.keys.get(1);
		piv[1] = this.cur.offset;
	}

	public void splitNonLeafNode() throws IOException{
		piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
		piv[1] = this.offset + 1;
		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		this.offset++;
		this.cur = new nonleafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);
		TinySEBPlusTree.tree_buffer.clear();
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
	}
	
	
	public void splitRootNode() throws IOException {
		this.cur.status = 1;
		piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
		piv[1] = this.offset + 1;
		
		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
		
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		
		int cur_offset = this.cur.offset;
		this.offset++;
		this.cur = new nonleafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);
		TinySEBPlusTree.tree_buffer.clear();
//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		
		this.offset++;
		this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset);
		this.cur.vals.add(cur_offset);
		this.cur.insert(piv[0], piv[1]);
		this.root = this.cur.copyNode();

//		cache.put(this.cur.offset, this.cur.copyNode());
		writeFile(this.cur);
		
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
//		this.cur = cache.get(point);
		this.cur = readFile(point);
		
	}
	public void search_leaf_node(int key) throws IOException {
		this.cur = this.root;
		while(this.cur.status != 2) {
			get_child(key);
		}
	}
	public void get_child(int key) throws IOException {
		int point = this.cur.get_value(key);
//		this.cur = cache.get(point);
		this.cur = readFile(point);
//		this.cache.add(TinySEBPlusTree.readFile(point));
		this.pedigree.push(this.cur);
	}

	public static void printarray(byte[] array) {
		ByteBuffer bf = ByteBuffer.wrap(array);
		for(int i=0; i < array.length/4; i++) {
			System.out.print(bf.getInt()+", ");
		}
		System.out.println();
	}
//	public void printarray() {
//		
//		for(int i : this.cur.to_tree_buffer().array()) {
//			System.out.print(i+", ");
//		}
//		System.out.println();
//	}
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
//		
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

