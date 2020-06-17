//package edu.hanyang.submit;
//
//import edu.hanyang.indexer.BPlusTree;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.ByteBuffer;
//import java.util.*;
//
//public class TinySEBPlusTree implements BPlusTree{
//	//-XX:+HeapDumpOnOutOfMemoryError
//	final static int metasize = 12;
//	static int blocksize;
//	static int nblocks;
//	static int fanout;
//	
//	private static RandomAccessFile tree;
//	private static RandomAccessFile meta;
//	
//	Node cur;
//	Node root;
//	int offset=1;
//	Stack<Node> pedigree = new Stack<>();
//
//	static int[] piv;
//	static ByteBuffer tree_buffer;
//	static ByteBuffer meta_buffer;
//	
//	public void printoffset() {
//		System.out.println("offset : " + this.offset);
//	}
//	/*이부분은 tmp 파일 비우는 용도*/
//	public static void clean(String dir) {
//		File file = new File(dir);
//		File[] tmpFiles = file.listFiles();
//		if (tmpFiles != null) {
//			for (int i = 0; i < tmpFiles.length; i++) {
//				if (tmpFiles[i].isFile()) {
//					tmpFiles[i].delete();
//				} else {
//					clean(tmpFiles[i].getAbsolutePath());
//				}
//				tmpFiles[i].delete();
//			}
//			file.delete();
//		}
//	}
//	public static void init() {
//		clean("./tmp");
//		File treefile = new File("./tree.tree");
//		File metafile = new File("./meta.meta");
//		if(treefile.exists()) {
//			treefile.delete();
//		}
//		if(metafile.exists()) {
//			metafile.delete();
//		}
//	}
//	public static void make_tmp(String tmdir) {
//		File Folder = new File(tmdir);
//
//		if (!Folder.exists()) {
//			try {
//				Folder.mkdir();
//			} catch(Exception e) {
//				e.getStackTrace();
//			}
//		}
//	}
//	public static void writeFile(Node node)
//			throws IOException{
//		byte[] tree_buffer = node.to_tree_buffer().array();
//		byte[] meta_buffer = node.to_meta_buffer().array();
//		int offset = node.offset;
//		
//		TinySEBPlusTree.tree.seek(offset*TinySEBPlusTree.blocksize);
//		TinySEBPlusTree.tree.write(tree_buffer);
//		TinySEBPlusTree.meta.seek(offset*TinySEBPlusTree.metasize);
//		TinySEBPlusTree.meta.write(meta_buffer);
//		
//		if(node.status == 0) {
//			TinySEBPlusTree.tree.seek(0);
//			TinySEBPlusTree.tree.write(tree_buffer);
//			TinySEBPlusTree.meta.seek(0);
//			TinySEBPlusTree.meta.write(meta_buffer);
//		}
//	}
//	
//	/* size : stored node의 사이즈 */
//	public static Node readFile(int position) throws IOException {
//		byte[] buffer = new byte[TinySEBPlusTree.metasize];
//		TinySEBPlusTree.meta.seek(position*TinySEBPlusTree.metasize);
//		TinySEBPlusTree.meta.read(buffer);
//		ByteBuffer bf = ByteBuffer.wrap(buffer);
//		int meta_zero = bf.getInt();
//		int meta_one = bf.getInt();
//		int meta_two = bf.getInt();
//		bf.clear();
//		buffer = new byte[meta_two*4];
//		TinySEBPlusTree.tree.seek(position * TinySEBPlusTree.blocksize);
//		TinySEBPlusTree.tree.read(buffer);
//		bf = ByteBuffer.wrap(buffer);
//		if(meta_zero == 2) {
//			return new leafNode(bf, TinySEBPlusTree.blocksize, meta_zero, meta_one);
//		}else {
//			return new nonleafNode(bf, TinySEBPlusTree.blocksize, meta_zero, meta_one);
//		}
//	}
//	
//
//	
//
//	
//
//	@Override
//	public void close() throws IOException {
//		pedigree.clear();
//		TinySEBPlusTree.tree.close();
//		TinySEBPlusTree.meta.close();
//		this.root = null;
//		this.cur = null;
//		
//		
//	}
//	public void printallnode() throws IOException {
//		System.out.println("currente offset : " + this.offset);
//		for(int i = 0; i <= this.offset; i++) {
//			Node node;
//			node = TinySEBPlusTree.readFile(i);
//			node.set_node_size();
//			System.out.printf("offset(%d) node(%d) : ", i, node.node_size);
//			printarray(node.to_tree_buffer().array());
//			
//		}
//	}
//
//	@Override
//	public void insert(int key, int val) throws IOException{
////		System.out.printf("====insert %d, %d===", key, val);
////		System.out.println();
//		if(this.root != null) {
//			this.pedigree.push(this.root);
//			this.search_leaf_node(key);
//			this.cur.insert(key, val);
//			if(!this.cur.isOver()) {
////				cache.put(this.cur.offset, this.cur);
//				writeFile(this.cur);
//				this.pedigree.clear();
//				this.cur.keys.clear();
//				this.cur.vals.clear();
////				this.printallnode();
//				return;
//			}
//			this.splitLeafNode();
//			this.pedigree.pop();
//			while(this.pedigree.size() > 1) {
//				this.cur = this.pedigree.pop();
//				this.cur.insert(piv[0], piv[1]);
//				if(!this.cur.isOver()) {
////					cache.put(this.cur.offset, this.cur);
//					writeFile(this.cur);
//					this.pedigree.clear();
//					this.cur.keys.clear();
//					this.cur.vals.clear();
////					this.printallnode();
//					return;
//				}
//				this.splitNonLeafNode();
//			}
//			//root
//			this.cur = this.pedigree.pop();
//			this.cur.insert(piv[0], piv[1]);
//			if(!this.cur.isOver()) {
////				cache.put(this.cur.offset, this.cur);
//				writeFile(this.cur);
//				this.pedigree.clear();
//	//			this.printallnode();
//				return;
//			}
//			this.splitRootNode();
//			this.pedigree.clear();
////			this.printallnode();
//			return;
//		} else {
//			this.cur.insert(key, val);
//			if(!this.cur.isOver()) {
////				cache.put(this.cur.offset, this.cur);
//				writeFile(this.cur);
//				
////				System.out.printf("offset(%d) node(%s) : ", this.cur.offset, this.cur.node_size);
////				this.printarray(this.cur.leaf_to_tree_buffer());
//				return;
//			}
//			this.splitLeafNode();
//			this.offset++;
//			this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset); //non leaf 
//			this.cur.vals.add(this.offset - 2);
//			this.cur.insert(piv[0], piv[1]);
//			this.root = this.cur.copyNode();		
////			cache.put(this.cur.offset, this.cur);
//			writeFile(this.cur);
//			this.cur.keys.clear();
//			this.cur.vals.clear();
////			this.printallnode();
//			return;
//		}
//		
//	}
//
//	//node = origin, root = new, leaf = new
//	
//	//node = Origin, newnode = new
//	@Override
//	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
//		String tmpdir = "./tmp/";
//		make_tmp(tmpdir);
//		TinySEBPlusTree.tree = new RandomAccessFile(filepath, "rw");
//		TinySEBPlusTree.meta = new RandomAccessFile(metafile, "rw");
//		int adv;
//		if(blocksize > 32) {
//			adv = 10;
//		} else {
//			adv = 1;
//		}
//		TinySEBPlusTree.blocksize = blocksize/adv;
//		TinySEBPlusTree.nblocks = nblocks;
//		/*fanout setting*/
//		int num_keys = (TinySEBPlusTree.blocksize / Integer.BYTES) / 2;
//		TinySEBPlusTree.fanout = num_keys * 1 / 2 + 1;
//		piv = new int[2];
//		if(TinySEBPlusTree.tree.length() != 0) {
//			this.root = TinySEBPlusTree.readFile(0);
//		} else {
//			this.cur = new leafNode(TinySEBPlusTree.blocksize, 2, this.offset);
//		}
//	}
//
//
//	public void splitLeafNode() throws IOException {
////		this.fanout = this.cur.max_num / 2 + 1;
//		
//		/* cur node 작업 */
//		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		
//		this.offset++;
//		this.cur = new leafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 2, this.offset); //leaf node
//		TinySEBPlusTree.tree_buffer.clear();
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		piv[0] = this.cur.keys.get(1);
//		piv[1] = this.cur.offset;
//	}
//
//	public void splitNonLeafNode() throws IOException{
//		piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
//		piv[1] = this.offset + 1;
//		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		this.offset++;
//		this.cur = new nonleafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);
//		TinySEBPlusTree.tree_buffer.clear();
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//	}
//	
//	
//	public void splitRootNode() throws IOException {
//		this.cur.status = 1;
//		piv[0] = this.cur.keys.get(TinySEBPlusTree.fanout);
//		piv[1] = this.offset + 1;
//		
//		TinySEBPlusTree.tree_buffer = this.cur.to_tree_buffer(TinySEBPlusTree.fanout);
//		
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		
//		int cur_offset = this.cur.offset;
//		this.offset++;
//		this.cur = new nonleafNode(TinySEBPlusTree.tree_buffer, TinySEBPlusTree.blocksize, 1, this.offset);
//		TinySEBPlusTree.tree_buffer.clear();
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		
//		this.offset++;
//		this.cur = new nonleafNode(TinySEBPlusTree.blocksize, 0, this.offset);
//		this.cur.vals.add(cur_offset);
//		this.cur.insert(piv[0], piv[1]);
//		this.root = this.cur.copyNode();
//
////		cache.put(this.cur.offset, this.cur.copyNode());
//		writeFile(this.cur);
//		
//	}
//
//	//status, offset 순으로 저장함
//	@Override
//	public int search(int key) throws IOException {
//		this.cur = this.root;
//		search_leaf_node_(key);
//		int result = this.cur.get_value(key);
//		this.cur = null;
//		return result;
//		
//	}
//
//	//leaf 찾기
//	public void search_leaf_node_(int key) throws IOException {
//		while(this.cur.status != 2) {
//			this.get_child_(key);
//		}
//	}
//	public void get_child_(int key) throws IOException {
//		int point = this.cur.get_value(key);
////		this.cur = cache.get(point);
//		this.cur = readFile(point);
//		
//	}
//	public void search_leaf_node(int key) throws IOException {
//		this.cur = this.root;
//		while(this.cur.status != 2) {
//			get_child(key);
//		}
//	}
//	public void get_child(int key) throws IOException {
//		int point = this.cur.get_value(key);
////		this.cur = cache.get(point);
//		this.cur = readFile(point);
////		this.cache.add(TinySEBPlusTree.readFile(point));
//		this.pedigree.push(this.cur);
//	}
//
//	public static void printarray(byte[] array) {
//		ByteBuffer bf = ByteBuffer.wrap(array);
//		for(int i=0; i < array.length/4; i++) {
//			System.out.print(bf.getInt()+", ");
//		}
//		System.out.println();
//	}
////	public void printarray() {
////		
////		for(int i : this.cur.to_tree_buffer().array()) {
////			System.out.print(i+", ");
////		}
////		System.out.println();
////	}
//	public static void main(String[] args) throws IOException {
//		//mvn clean -D test=BPlusTreeTest#bPlusTreeTestWithLargeFile test
//		//mvn clean -D test=BPlusTreeTest#bPlusTreeTest test
//		init();
//		String metapath = "./tmp/bplustree.meta";
//		String savepath = "./tmp/bplustree.tree";
////		String testfile = "/home/hms/Desktop/TinySE-submit/src/test/resources/stage3-500000.data";
//		int nblocks = 32;
//		int blocksize = 32;
//		TinySEBPlusTree tree = new TinySEBPlusTree();
//		tree.open(metapath, savepath, blocksize, nblocks);
//		tree.insert(5, 10);
//		tree.insert(6, 15);
//		tree.insert(4, 20);
//		tree.insert(7, 1);
//		tree.insert(8, 5);
//		tree.insert(17, 7);
//		tree.insert(30, 8);
//		tree.insert(1, 8);
//		tree.insert(58, 1);
//		tree.insert(25, 8);
//		tree.insert(96, 32);
//		tree.insert(21, 8);
//		tree.insert(9, 98);
//		tree.insert(57, 54);
//		tree.insert(157, 54);
//		tree.insert(247, 54);
//		tree.insert(357, 254);
//		tree.insert(557, 54);
//		tree.insert(600, 23);
//		
//		tree.close();
////		
//		tree = new TinySEBPlusTree();
//		tree.open(metapath, savepath, blocksize, nblocks);
//		
//		System.out.println("key : 5 -> " + tree.search(5) +" | " + 10);
//		System.out.println("key : 6 -> " + tree.search(6)+" | " +  15);
//		System.out.println("key : 4 -> " + tree.search(4)+" | " +  20);
//		System.out.println("key : 7 -> " + tree.search(7)+" | " +  1);
//		System.out.println("key : 8 -> " + tree.search(8)+" | " +  5);
//		System.out.println("key : 17-> " + tree.search(17)+" | " +  7);
//		System.out.println("key : 30-> " + tree.search(30)+" | " +  8);
//		System.out.println("key : 1 -> " + tree.search(1)+" | " +  8);
//		System.out.println("key : 58-> " + tree.search(58)+" | " +  1);
//		System.out.println("key : 25-> " + tree.search(25)+" | " +  8);
//		System.out.println("key : 96-> " + tree.search(96)+" | " +  32);
//		System.out.println("key : 21-> " + tree.search(21)+" | " +  8);
//		System.out.println("key : 9 -> " + tree.search(9)+" | " +  98);
//		System.out.println("key : 57-> " + tree.search(57)+" | " +  54);
//		System.out.println("key :157-> " + tree.search(157)+" | " +  54);
//		System.out.println("key :247-> " + tree.search(247)+" | " +  54);
//		System.out.println("key :357-> " + tree.search(357)+" | " +  254);
//		System.out.println("key :557-> " + tree.search(557)+" | " +  54);
//		System.out.println("key :558-> " + tree.search(600)+" | " +  23);
//	
//		
//		tree.close();
//	}
//}



package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;

public class TinySEBPlusTree implements BPlusTree{

	int blockSize;
	int nblocks;
	int cacheSize;
	int fanout;
	int mode;
	int depth;
	int cur;
	int nodecnt;
	
	int rootPos;
	Node root;
	LRU cache;
	float adjust;
	
	static byte[] bytes;
	static ByteBuffer buffer;

	String treepath;
	String metapath;
	RandomAccessFile tree;
	RandomAccessFile meta;
	
	public class LRU{
		class Data {
	        int key;
	        Node node;
	        public Data(int key, Node node){
	            this.key = key;
	            this.node = node;
	        }
	    }
		int mode;
		int capacity;
		LinkedHashMap<Integer, Data> map;
		
		public LRU(int capacity, int mode) {
			this.capacity=capacity;
			this.mode = mode;
			this.map = new LinkedHashMap<Integer, Data>(capacity,.75f,true) {
				@Override protected boolean removeEldestEntry(Map.Entry<Integer, Data> eldest) {
					if(size() == capacity)
						try {
							LRU.this.RemoveNode(eldest);
						} catch(IOException e) {
							
						}
					return size() > LRU.this.capacity;
				}
			};
		}
		
		public void RemoveNode(Map.Entry<Integer, Data> entry) throws IOException{
			int key = entry.getKey();
			Node node = entry.getValue().node;
			map.remove(key);
			if(mode == 1) node.writeData(node.pos);
		}
		
		public Node GetNode(int key) throws IOException {
	        if(map.containsKey(key))
	            return map.get(key).node;
	        else {
	            Node node = MakeNode(key);
	            SetNode(key, node);
	            return node;
	        }
	    }
		public void SetNode(int key, Node value) throws IOException {
			if(map.containsKey(key))
				map.get(key);
			else {
				Data n = new Data(key,value);
				map.put(key,n);
			}
		}
		
		public void Flush() throws IOException{
			map.forEach((key,value)->{
				try {
					value.node.writeData(value.node.pos);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	class Node {

		int keySize;
		int pos;
		int[] keys;
		int[] values;
		boolean isLeaf;
		
		Node(boolean isLeaf, int pos) throws IOException {

			this.isLeaf = isLeaf;
			this.keys = new int[fanout-1];
			this.values = new int[fanout];
			if(pos==-1) {
				this.pos = nodecnt * blockSize;
				this.keySize = 0;
			}
			else {
				this.pos = pos;
				tree.seek(this.pos);
				tree.read(bytes);
				   
				buffer.clear();
				buffer.getInt();
				this.keySize = buffer.getInt();
				
				for (int idx = 0; idx < keySize; ++idx) {
					this.keys[idx] = buffer.getInt();
					this.values[idx] = buffer.getInt();
				}
				
				if (!isLeaf && keySize != 0)
					this.values[keySize] = buffer.getInt();
			}
		}
		
		public boolean nodeIsFull() {
			return keySize == fanout-1;
		}
		
		public void writeData(int offset) throws IOException {
		
			buffer.clear();
			buffer.putInt(this.isLeaf ? 1 : 0);
			buffer.putInt(this.keySize);
			
			for (int idx = 0; idx < this.keySize; ++idx) {
				buffer.putInt(this.keys[idx]);
				buffer.putInt(this.values[idx]);
			}
				
			if (!isLeaf && keySize > 0)
				buffer.putInt(this.values[keySize]);
			
			tree.seek(this.pos);
			tree.write(bytes);
		}

		public int Search(int key) throws IOException {
			
			if(this.isLeaf) {
				int pos = Arrays.binarySearch(keys, 0, keySize, key);
				return pos >= 0 ? values[pos] : -1;
			}
			else {
				Node node = GetChild(key, 0);
				return node.Search(key);
			}
		}

		public int Split() throws IOException {

			nodecnt++;
			Node newNode = new Node(this.isLeaf,-1);
			
			int start = keySize/2;
			int end = keySize;
			
			int leafInt = this.isLeaf ? 0 : 1;
			for(int idx = 0; idx < end - start - leafInt; ++idx)
				newNode.keys[idx] = keys[idx + start + leafInt];
			
			newNode.keySize = end - (start + leafInt);
			this.keySize -= end - (start + leafInt);
			
			for(int idx = 0; idx < end - start; ++idx)
				newNode.values[idx] = values[idx + start + leafInt];
			
			cache.SetNode(newNode.pos, newNode);
			return keys[start];
		}
		
		public void InsertValue(int key, int value) throws IOException {
			if(this.isLeaf) {
				
				int pos = Arrays.binarySearch(keys ,0 ,keySize, key);
				int valueIndex = pos >= 0 ? pos : -pos - 1;
				
				for(int idx = this.keySize - 1; idx >= valueIndex; idx--){
					this.keys[idx + 1] = this.keys[idx];
					this.values[idx + 1] = this.values[idx];
				}
				   
				this.keySize++;
				this.keys[valueIndex] = key;
				this.values[valueIndex] = value;
				cache.SetNode(this.pos, this);
			    
			} else {
				Node childNode = GetChild(key, 1);
				childNode.InsertValue(key, value);
				    
				if (childNode.nodeIsFull()) {
					int leftdata = childNode.Split();
					int pos = nodecnt * blockSize;
					InsertNode(leftdata, pos);
					cache.SetNode(this.pos, this);
				}
			}
		
			if( root.nodeIsFull() ) {
				nodecnt++;
				Node newRootNode = new Node(false,-1);
				newRootNode.keys[0] = Split();
				newRootNode.keySize++;
				newRootNode.values[0] = this.pos;
				newRootNode.values[1] = nodecnt * blockSize;
				root = newRootNode;
			}
		}
		
		public void InsertNode(int key, int childOffset) {
			
			int pos = Arrays.binarySearch(keys,0,keySize, key);
			int childIndex = pos >= 0 ? pos + 1 : -pos - 1;
			
			for(int idx = keySize-1; idx >= childIndex; --idx){
				this.keys[idx + 1] = this.keys[idx];
				this.values[idx + 2] = this.values[idx + 1];
			}
			
			this.keySize++;
			this.keys[childIndex] = key;
			this.values[childIndex + 1] = childOffset;
		    
		}
		
		Node GetChild(int key, int isInsert) throws IOException {
			 
			int pos = Arrays.binarySearch(this.keys,0, this.keySize, key);
			int childIndex = pos >= 0 ? ( pos + 1 ): (-pos - 1);
			int childValue = this.values[childIndex];
			return cache.GetNode(childValue);
		    
		}
	}
	
	@Override
	public void close() throws IOException {
		
		root.writeData(root.pos);
		cache.Flush();
		tree.close();
		
		meta.writeInt(root.pos);
		meta.writeInt(fanout);
		meta.writeInt(blockSize);
		meta.close();
	}

	@Override
	public void insert(int key, int value) throws IOException {
		root.InsertValue(key, value);
	}

	@Override
	public void open(String metapath, String treepath, int blockSize, int nblocks) throws IOException {
		
		this.treepath = treepath;
		this.metapath = metapath;
		int adjust = (blockSize*nblocks) / 2048000;
		blockSize=(int)(blockSize/adjust);
		nblocks=(int)(nblocks*adjust);
		
		File tmpdir = new File("./tmp");
		File treeFile = new File(this.treepath);
		File metaFile = new File(this.metapath);
		
		if (!tmpdir.exists())
			tmpdir.mkdir();
		if(!treeFile.exists())
			treeFile.createNewFile();
		
		this.meta = new RandomAccessFile(metaFile, "rw");
		this.tree = new RandomAccessFile(treeFile, "rw");
		
		if(metaFile.exists() && metaFile.length() > 0){
			this.mode = 0;
			this.rootPos = this.meta.readInt();
			this.fanout = this.meta.readInt();
			this.blockSize = this.meta.readInt();
		}
		else{
			this.mode = 1;
			metaFile.createNewFile();
			this.rootPos = 0;
			this.fanout = blockSize/(Integer.SIZE/4)-1;
			this.blockSize = blockSize;
		}
		
		cache = new LRU(nblocks, this.mode);
		
		if(tree.length() > 0)
			root = MakeNode(rootPos);
		else root = new Node(true,-1);

		bytes = new byte[this.blockSize];
		buffer = ByteBuffer.wrap(bytes);
	}

	@Override
	public int search(int key) throws IOException {
		return root.Search(key);
	}
	
	public void init() {
		
	}

	Node MakeNode(int offset) throws IOException{
		this.tree.seek(offset);
		int isLeaf = tree.readInt();
		return new Node(isLeaf == 1, offset);
	}
}