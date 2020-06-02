//package edu.hanyang.submit;
//
//import edu.hanyang.indexer.BPlusTree;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.ByteBuffer;
//import java.util.*;
//
//public class TinySEBPlusTree implements BPlusTree{
////	static String filepath ="./tmp/node.meta";
////	static String Nfilepath = "./tmp/node.data";
//	static String metapath = "./tmp/bplustree.meta";
//	static String savepath = "./tmp/bplustree.tree";
//	static int blocksize=4096;
//	static int nblocks;
//	static boolean Root = true;
//	static List<Node> Cashe= new ArrayList<Node>();
////	File treefile = new File(savepath);
////	File metafile = new File(metapath);
//	
//	static RandomAccessFile tree;
//	static RandomAccessFile meta;
//
//	static int offset=1;
//	
//	static Node node = new Node(blocksize,offset,3); //Cusor Nodes
//	public static void main(String[] args) throws IOException {
//		File treefile = new File(savepath);
//		File metafile = new File(metapath);
//		
//		if (treefile.exists()) {
//			if (! treefile.delete()) {
//				System.err.println("error: cannot remove tree file");
//				System.exit(1);
//			}
//		}
//		if (metafile.exists()) {
//			if (! metafile.delete()) {
//				System.err.println("error: cannot remove meta file");
//				System.exit(1);
//			}
//		}
//		TinySEBPlusTree Tree = new TinySEBPlusTree();
//		Tree.open(metapath, savepath, blocksize, nblocks);
//		System.out.println("Starting");
//		
//		Tree.insert(5, 10);
//		Tree.insert(6, 15);
//		Tree.insert(4, 20);
//		Tree.insert(7, 1);
//		Tree.insert(8, 5);
//		Tree.insert(17, 7);
//		Tree.insert(30, 8);
//		Tree.insert(1, 8);
//		Tree.insert(58, 1);
//		Tree.insert(25, 8);
//		Tree.insert(96, 32);
//		Tree.insert(21, 8);
//		Tree.insert(9, 98);
//		Tree.insert(57, 54);
//		Tree.insert(157, 54);
//		Tree.insert(247, 54);
//		Tree.insert(357, 254);
//		Tree.insert(557, 54);
//		Tree.insert(10, 13);
//		Tree.insert(11, 514);
//		Tree.insert(12, 599);
//		Tree.insert(13, 19);
//		Tree.insert(14, 111);
//		Tree.insert(15, 899);
//		Tree.insert(16, 99);
//		Tree.insert(24, 112);
//		Tree.insert(23, 119);
//		Tree.insert(43, 541);
//		Tree.insert(45, 234);
//		Tree.insert(62, 51);
//		Tree.insert(111, 23);
//		Tree.insert(114, 14);
//		Tree.insert(215, 919);
//		Tree.insert(132, 129);
//		Tree.insert(324, 131);
//		Tree.insert(199, 89);
//		Tree.insert(200, 90);
//		Tree.insert(300, 110);
//		Tree.insert(400, 120);
//		
//		
////		
//		Tree.close();
//		Tree.open(metapath, savepath, blocksize, nblocks);
//		System.out.println("Valuse : "+Tree.search(5));
//		System.out.println("Valuse : "+Tree.search(6));
//		System.out.println("Valuse : "+Tree.search(4));
//		System.out.println("Valuse : "+Tree.search(7));
//		System.out.println("Valuse : "+Tree.search(8));
//		System.out.println("Valuse : "+Tree.search(17));
//		System.out.println("Valuse : "+Tree.search(30));
//		System.out.println("Valuse : "+Tree.search(1));
//		System.out.println("Valuse : "+Tree.search(58));
//		System.out.println("Valuse : "+Tree.search(25));
//		System.out.println("Valuse : "+Tree.search(96));
//		System.out.println("Valuse : "+Tree.search(21));
//		System.out.println("Valuse : "+Tree.search(9));
//		System.out.println("Valuse : "+Tree.search(57));
//		System.out.println("Valuse : "+Tree.search(157));
//		System.out.println("Valuse : "+Tree.search(247));
//		System.out.println("Valuse : "+Tree.search(357));
//		System.out.println("Valuse : "+Tree.search(557));
//		System.out.println("Valuse : "+Tree.search(10));
//		System.out.println("Valuse : "+Tree.search(11));
//		System.out.println("Valuse : "+Tree.search(12));
//		System.out.println("Valuse : "+Tree.search(13));
//		System.out.println("Valuse : "+Tree.search(14));
//		System.out.println("Valuse : "+Tree.search(15));
//		System.out.println("Valuse : "+Tree.search(16));
//		System.out.println("Valuse : "+Tree.search(24));
//		System.out.println("Valuse : "+Tree.search(23));
//		System.out.println("Valuse : "+Tree.search(43));
//		System.out.println("Valuse : "+Tree.search(45));
//		System.out.println("Valuse : "+Tree.search(62));
//		System.out.println("Valuse : "+Tree.search(111));
//		System.out.println("Valuse : "+Tree.search(114));
//		System.out.println("Valuse : "+Tree.search(215));
//		System.out.println("Valuse : "+Tree.search(132));
//		System.out.println("Valuse : "+Tree.search(324));
//		System.out.println("Valuse : "+Tree.search(199));
//		System.out.println("Valuse : "+Tree.search(200));
//		System.out.println("Valuse : "+Tree.search(300));
//		System.out.println("Valuse : "+Tree.search(400));
//		System.out.println("Valuse : "+Tree.search(40));
////		
//		
//		System.out.println("Complete");
//////		
////		String file_ = "./src/test/resources/stage3-15000000.data";
////		
////		DataInputStream is = new DataInputStream(
////									new BufferedInputStream(
////											new FileInputStream(file_), blocksize));
////		
////		long timestamp = System.currentTimeMillis();
////		int Size = is.available();
////		while(Size != 0) {
////			int key = is.readInt();
////			int val = is.readInt();
////			
////			Tree.insert(key, val);
////			
////			
////		}
////		
////		System.out.println("done time : " + (System.currentTimeMillis() - timestamp));
////		
//	}
//	
//		
//		
//	public static String make_dir(String tmdir, int step) {
//		String path = tmdir+File.separator+String.valueOf(step);
//		File Folder = new File(path);
//		
//		if (!Folder.exists()) {
//			try {
//				Folder.mkdir();
//			} catch(Exception e) {
//				e.getStackTrace();
//			}
//		}
//		return path;
//	}
//	
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
//		
//	}
//	
//	private static int[] readFromFile(RandomAccessFile file, int position, int size)
//		throws IOException {
//		int node_size = blocksize/Integer.BYTES;
//		byte[] bytebuffer = new byte[node_size*4];
//		int[] buffer = new int[node_size];
//		file.seek(position);
//		file.read(bytebuffer);
//		
//		ByteBuffer bf = ByteBuffer.wrap(bytebuffer);
//		
//		for(int i = 0; i < node_size; i++) {
//			buffer[i] = bf.getInt();
//		}
//		return buffer;
//	}
//	
//	private static int[] readFromMeta(RandomAccessFile file, int position, int size)
//		throws IOException {
//		file.seek(position);
//		int[] integers = new int[size/Integer.BYTES];
//		for(int i = 0; i < integers.length; i++) {
//			integers[i] = file.readInt();
//		}
////		file.close();
//		return integers;
//	}
//	
//	private static void writeToFile(RandomAccessFile file, int[] buffer, int position)
//		throws IOException{
//		
//		file.seek(position);
//		byte[] bytebuffer = new byte[buffer.length*4];
//		for(int i = 0; i < buffer.length; i++) {
//			System.arraycopy(intTobyte(buffer[i]), 0, bytebuffer, i*4, 4);
//		}
//		file.write(bytebuffer);
////		file.close();
//	}
//	public static  byte[] intTobyte(int value) {
//		byte[] byteArray = new byte[4];
//		byteArray[0] = (byte)(value >> 24);
//		byteArray[1] = (byte)(value >> 16);
//		byteArray[2] = (byte)(value >> 8);
//		byteArray[3] = (byte)(value);
//		return byteArray;
//	}
//
//	@Override
//	public void close() throws IOException {
//		// TODO Auto-generated method stub
//		tree.close();
//		meta.close();
//	}
//
//	@Override
//	public void insert(int key, int val) throws IOException{
//
////		System.out.println("----------------insert Start-----------Key : "+key+"Val : "+val);
//		if(!isRootNode(node)) {
////			System.out.println("Not Root");
////			System.out.println("Current status : "+node.status);
//			searchRoot();
//			if(!isLeafNode(node)) {
////				System.out.println("Current status : "+node.status);
//				searchLeaf(key);
//			}
//			//node초기화
//		}
////		System.out.println("kkkk1");
//		init_insert(node, key, val);
////		System.out.println("kkkk2");
//		Split(node);
//		
////		System.out.println("Cashe Size : "+Cashe.size());
////		System.out.println("----------------insert Ends---------------");
//		//node 변경하기
////		int[] Array = new int[1];
////		node = new Node(Array, blocksize, 2,0);
//	}
//	
//	@Override
//	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
//		String tmpdir = "./tmp/";
//		make_tmp(tmpdir);
//		tree = new RandomAccessFile(filepath, "rw");
//		meta = new RandomAccessFile(metafile, "rw");
//		this.blocksize = blocksize;
//		this.nblocks = nblocks;
//	}
//
//	@Override
//	public int search(int key) throws IOException {
//		// TODO Auto-generated method stub
////		System.out.println("starting");
//		if(!isRootNode(node)) {
////			System.out.println("Not Root");
//			searchRoot();//root로 변경
//			if(!isLeafNode(node)) {
//				searchLeaf(key);// leaf로 변경
//			}
//		}
////		System.out.println("Keys :"+node.keys);
////		System.out.println("Vals :"+node.vals);
//		int i;
////		System.out.println("Found Leaf Node Key : "+node.keys);
//		for(i=0;i<node.keys.size();i++) {
//			if(node.keys.get(i)==key) {
//				
//				return node.vals.get(i);
//			}
//		
//		}
////		System.out.println("Do not find it!");
//		return -1;
//		
//	}
//	
//	public static boolean isLeafNode(Node node) {
//		if(node.status == 2||node.status==3)
//			return true;
//		return false;
//	}
//	public static boolean isRootNode(Node node) {
//		if(node.status == 0||node.status==3)
//			return true;
//		return false;
//	}
//	public static boolean isFull(Node node) {
//		if(node.keys.size() == (blocksize-8)/(Integer.BYTES*2)) return true;
//		return false;
//	}
//
//	//status, offset 순으로 저장함
//	public static void searchRoot() throws IOException {
//		if(!Root) {
//			node = Cashe.get(0);
//			Node tmp = node;
//			Cashe.removeAll(Cashe);
//			Cashe.add(tmp);
//			return ;
//		}
//		//root 변동 있을 때 Cashe 다시 만들기.
//		Cashe.removeAll(Cashe);
//		int[] offsets = readFromMeta(meta, 0, 8);
//		
////		System.out.println("what the offset? : "+ offsets[1]);
//		int[] Integers = readFromFile(tree, offsets[1]*blocksize,blocksize);
//
//		node = new Node(Integers, blocksize, offsets[1],  offsets[0]);
////		System.out.println("find root : "+ node.keys);
////		System.out.println("find root : "+ node.vals);
//		Cashe.add(0,node);
//		Root = false; 
//	}
//	//leaf 찾기
//	public static void searchLeaf(int key)throws IOException {
//		Cashe.add(node);
////		System.out.println("History : "+history);
////		System.out.println("Founding LeafNode..");
//		int find_offset = 0;
////		System.out.println("Finding..... Current Node Offset : "+ node.offset);
//		for(int i=0;i<node.keys.size();i++) { //현재 Cur_node
//			if(key<node.keys.get(i)){
//				find_offset = node.vals.get(i);break;
//			}
//			
//		}
//		if(find_offset == 0) {//keys를 돌고 작은 값을 찾지 못하면 find_offset은 0 마지막 value값 참조해야함
//			find_offset = node.vals.get(node.vals.size()-1); //Vals중 가장 우측 값
//		}
//		int[] Status = readFromMeta(meta,find_offset*8,8);
//		if(Status[0]==2) {
//			int[] Nodes = readFromFile(tree, find_offset*blocksize, blocksize);
//			node = new Node(Nodes, blocksize, find_offset, Status[0]);
////			System.out.println("Found It!!!! it's offset :"+node.offset);
//			
//		}
//		else {
//			int[] Nodes = readFromFile(tree, find_offset*blocksize, blocksize);
//			node = new Node(Nodes, blocksize, find_offset, Status[0]);
//			searchLeaf(key);
//
//		}
//	}
//	
//	public static void init_insert(Node node, int key, int val) throws IOException{
////		System.out.println("Node Keys: "+node.keys);
////		System.out.println("Node Vals: "+node.vals);
//		if (node.keys.size() == 0) {
////			writeToFile(meta, node.to_meta_buffer(),0);
////			writeToFile(meta, node.to_meta_buffer(),node.offset*8);
//			node.keys.add(key);
//			node.vals.add(val);
//		}
//		//if (keys.size() == this.fanout) throw new Exception();
//		else {
//			Iterator<Integer> it = node.keys.iterator();
//			
//			while(it.hasNext()) {
//				int n = it.next();
//				int consist;
//				if(n > key ) {
//					consist = node.keys.indexOf(n);
//				
//					if(node.status==3||node.status==2) {
//						node.keys.add(node.keys.indexOf(n), key);
//						node.vals.add(consist, val);
//					}else {
//					
//					node.keys.add(node.keys.indexOf(n), key);
//					node.vals.add(consist+1, val);
//					}
//					writeToFile(tree, node.to_buffer(),node.offset*blocksize);
//					
//					
//					return ;
//				}
//			}
//			node.keys.add(key);
//			node.vals.add(val);
//		}
//		writeToFile(tree, node.to_buffer(),node.offset*blocksize);
//	}
//	
//	public static void Split(Node node) throws IOException{
//		
//		if(isFull(node)) {
//			if(isRootNode(node)) {
////				System.out.println("Node is Full1");
//				splitLeafNode(node);
//			}
//			else {
////				System.out.println("Node is Full2");
//				splitNonLeafNode(node);
//			}
//		}
//	}
//	public static void UpdateNode(Node node, Node parent, Node child) throws IOException{
//		//root일경우, root노드leaf노드(init경우) 같음
//		writeToFile(tree, node.to_buffer(),node.offset*blocksize);
//		writeToFile(tree, parent.to_buffer(),parent.offset*blocksize);
//		writeToFile(tree, child.to_buffer(),child.offset*blocksize);
//	}
//	public static void UpdateMeta(Node node, Node parent, Node child) throws IOException{
//		//root일경우, root노드leaf노드(init경우) 같음
//		writeToFile(meta, node.to_meta_buffer(),node.offset*8);
//		writeToFile(meta, parent.to_meta_buffer(),parent.offset*8);
//		writeToFile(meta, child.to_meta_buffer(),child.offset*8);
//	}
//	public static void PrintNodeTest(Node node, Node parent, Node child) {
//		System.out.println("node Keys# : "+node.keys);
//		System.out.println("node Vals# : "+node.vals);
//		
//		System.out.println("root Keys# : "+parent.keys);
//		System.out.println("root Vals# : "+parent.vals);
//		
//		System.out.println("leaf Keys# : "+child.keys);
//		System.out.println("leaf Vals# : "+child.vals);
//	}
//	public static void splitLeafNode(Node node) throws IOException {
//		if(isLeafNode(node)) { 
////			System.out.println("Select #1 : init");//root이면서 leaf인경우 무조건 leaf생성
//			Node root = new Node(blocksize, ++offset,0); //root로 생성
//			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2);
//			//분할
//			leaf.keys = node.keys.subList(node.keys.size()/2, node.keys.size());
//			leaf.vals = node.vals.subList(node.vals.size()/2, node.vals.size());
////			leaf.keys = node.getKeyList(node.keys.size()/2, node.keys.size()-1);
////			leaf.vals = node.getValList(node.vals.size()/2, node.vals.size()-1);
//			
//			node.keys = node.keys.subList(0, node.keys.size()/2);
//			node.vals = node.vals.subList(0, node.vals.size()/2);
////			node.keys = node.getKeyList(0, node.keys.size()/2-1);
////			node.vals = node.getValList(0, node.vals.size()/2);
//		
//			//중간 값 root 노드에 저장
//			root.keys.add(keytmp);
//			root.vals.add(node.offset);
//			root.vals.add(leaf.offset);
//			node.status = 2; // leaf로 변경
////			PrintNodeTest(node,root,leaf);
//			//노드 파일 업데이트. UPdateNode, UpdateMeta 둘다 순서 (origin, parent, child)
//			
//			UpdateNode(node,root,leaf);
//			UpdateMeta(node,root,leaf);
//			writeToFile(meta, root.to_meta_buffer(),0);
//			//메타 파일 업데이트.
////			writeToFile(meta, node.to_meta_buffer(),node.offset*8);
////			writeToFile(meta, root.to_meta_buffer(),0);
////			writeToFile(meta, root.to_meta_buffer(),root.offset*8);
////			writeToFile(meta, leaf.to_meta_buffer(),leaf.offset*8);
//		}
//		else {//root일 경우 무조건 Non-leaf생성 
////			System.out.println("Select #2 : root");
//			
//			Node root = new Node(blocksize, ++offset,0); //root로 생성
//			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2);
//			//분할
//			
////			Nonleaf.keys = node.getKeyList(node.keys.size()/2+1, node.keys.size()-1);
////			Nonleaf.vals = node.getValList(node.vals.size()/2, node.vals.size()-1);
//			Nonleaf.keys = node.keys.subList(node.keys.size()/2+1, node.keys.size());
//			Nonleaf.vals = node.vals.subList(node.vals.size()/2, node.vals.size());
//
//			node.keys = node.keys.subList(0, node.keys.size()/2);
//			node.vals = node.vals.subList(0, node.vals.size()/2);
//			
////			node.keys = node.getKeyList(0, node.keys.size()/2-1);
////			node.vals = node.getValList(0, node.vals.size()/2-1);
//			
//			root.keys.add(keytmp);
//			root.vals.add(node.offset);
//			root.vals.add(Nonleaf.offset);
//			node.status = 1; // Nonleaf로 변경
////			PrintNodeTest(node,root,Nonleaf);
//			Root = true; // 루트 변동 생김. SearchRoot 실행시 Cashe다시 찾아야함.
//			//노드 파일 업데이트. 
//			UpdateNode(node, root, Nonleaf);
//			UpdateMeta(node, root, Nonleaf);
//			//루트는 항상 맨위에 8byte만큼 저장
//			writeToFile(meta, root.to_meta_buffer(),0);
////			writeToFile(meta, root.to_meta_buffer(),root.offset*8);
////			writeToFile(meta, node.to_meta_buffer(),node.offset*8);
////			writeToFile(meta, Nonleaf.to_meta_buffer(),Nonleaf.offset*8);
//			
//		}
//	}
//	
//	public static void splitNonLeafNode(Node node) throws IOException{
//		if(isLeafNode(node)) { //leaf일 경우, 무조건 leaf만 생성
////			System.out.println("Select #3");
////			System.out.println("Now History : "+history);
//			int cur=Cashe.size()-1;
////			int[] NodeBuffer = readFromFile(tree, history.get(cur)*blocksize, blocksize);
////			int[] MetaBuffer= readFromMeta(meta,history.get(cur)*8,8);
//			
////			Node parent = new Node(NodeBuffer,blocksize,history.get(cur), MetaBuffer[0]); //parent node생성 leaf parent = non leaf
//			Node parent = Cashe.get(cur);
//			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
//			int valtmp=node.vals.get(node.keys.size()/2);
////			System.out.println("What is the Parent key Node? : "+parent.keys);
////			System.out.println("What is the Parent val Node? : "+parent.vals);
//			Cashe.remove(cur); //사용 offset은 삭제시키기
//			
//			//분할
////			leaf.keys = node.getKeyList(node.keys.size()/2, node.keys.size()-1);
////			leaf.vals = node.getValList(node.vals.size()/2, node.vals.size()-1);
//			leaf.keys = node.keys.subList(node.keys.size()/2, node.keys.size());
//			leaf.vals = node.vals.subList(node.vals.size()/2, node.vals.size());
//			
////			node.keys = node.getKeyList(0, node.keys.size()/2-1);
////			node.vals = node.getValList(0, node.vals.size()/2-1);
//			node.keys = node.keys.subList(0, node.keys.size()/2);
//			node.vals = node.vals.subList(0, node.vals.size()/2);
//			
//			init_insert(parent,keytmp, leaf.offset);
//			Split(parent);
////			insert(parent, keytmp,leaf.offset);
////			PrintNodeTest(node,parent,leaf);
//			UpdateNode(node,parent,leaf);
//			UpdateMeta(node,parent,leaf);
//			
//			//Update Meta
////			writeToFile(meta, leaf.to_meta_buffer(),leaf.offset*8);
////			writeToFile(meta, node.to_meta_buffer(),node.offset*8);
////			writeToFile(meta, parent.to_meta_buffer(),parent.offset*8);
//			
//		}
//		else { //leaf도, root도 아닐 경우(Nonleaf) 무조건 Nonleaf만 생성
////			System.out.println("Select #4");
////			System.out.println("Now History : "+history);
//			int cur =Cashe.size()-1;
////			int[] NodeBuffer = readFromFile(tree, history.get(cur)*blocksize, blocksize);
////			int[] MetaBuffer = readFromMeta(meta,history.get(cur)*8,8);
//			Node parent = Cashe.get(cur);
////			Node parent = new Node(NodeBuffer,blocksize,history.get(cur), MetaBuffer[0]); //parent node생성 leaf parent = non leaf
//			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
//			
//			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
//			Cashe.remove(cur);
//			//분할
////			Nonleaf.keys = node.getKeyList(node.keys.size()/2+1, node.keys.size()-1);
////			Nonleaf.vals = node.getValList(node.vals.size()/2, node.vals.size()-1);
//			Nonleaf.keys = node.keys.subList(node.keys.size()/2+1, node.keys.size());
//			Nonleaf.vals = node.vals.subList(node.vals.size()/2, node.vals.size());
//			
////			node.keys = node.getKeyList(0, node.keys.size()/2-1);
////			node.vals = node.getValList(0, node.vals.size()/2-1);
//			node.keys = node.keys.subList(0, node.keys.size()/2);
//			node.vals = node.vals.subList(0, node.vals.size()/2);
////			System.out.println("What is the Parent key Node? : "+parent.keys);
////			System.out.println("What is the Parent val Node? : "+parent.vals);
////			System.out.println("What is the Parent val Node? : "+parent.status);
//			
//			init_insert(parent,keytmp, Nonleaf.offset);
//			Split(parent);
//			
////			PrintNodeTest(node,parent,Nonleaf);
//			UpdateNode(node,parent,Nonleaf);
//			UpdateMeta(node,parent,Nonleaf);
////			
////			writeToFile(meta, node.to_meta_buffer(),node.offset*8);
////			writeToFile(meta, parent.to_meta_buffer(),parent.offset*8);
////			writeToFile(meta, Nonleaf.to_meta_buffer(),Nonleaf.offset*8);
//		}
//	}
//
//}
//
//class Node {
//	int offset;
//	
//	/* status 
//	 * 0 : root
//	 * 1 : non leaf
//	 * 2 : leaf
//	 */
//	int status; 
//	int max_keys;
//	
//	List<Integer> keys;
//	List<Integer> vals;
//	
//	
//	/*생성자는 2가지
//	 * stream을 input으로 받을때 <- search 할때 필요
//	 * 그냥 크기만큼 key, val을 생성 <- 새로운 노드를 만들때 필요 ex) split, write
//	 */
//	
//	//커서 만들기.
//	Node(int[] buffer, int blocksize, int offset, int status) {
////		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
//		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
//		int max_vals = max_keys + 1;
//		
//		this.max_keys = max_keys;
//		keys = new ArrayList<>(max_keys);
//		vals = new ArrayList<>(max_vals);
//		
//		this.offset = offset;
//		this.status = status;
//		int i;
//		for(i = 0; i < buffer.length / 2; i ++) {
//			if(buffer[i*2+1]<0||buffer[i*2]<0) break;
//			vals.add(buffer[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
//			keys.add(buffer[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
//		}
//		if(buffer[i*2]!=-1)vals.add(buffer[i*2]);
//	}
//	
//	Node(int blocksize, int offset, int status) {
//		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
//		int max_vals = max_keys + 1;
//		
//		this.max_keys = max_keys;
//		keys = new ArrayList<>(max_keys);
//		vals = new ArrayList<>(max_vals);
//		
//		this.offset = offset;
//		this.status = status;
//	}
//	
//	public boolean isFull() {
//		if(keys.size() == this.max_keys) return true;
//		
//		return false;
//	}
//
//	public int[] to_buffer() {
//		int[] buffer = new int[max_keys * 2 + 1];
////		System.out.println("what? max_keys:"+buffer.length);
//		int i;
////		System.out.println("init Key maximum : "+this.keys.size());
////		System.out.println("init Val maximum : "+this.vals.size());
//		for(i = 0; i < this.keys.size(); i++) {
//			buffer[i*2] = this.vals.get(i);
//			buffer[i*2 + 1] = this.keys.get(i);
//		}
//		if(i+1==vals.size()) {
//			buffer[i*2] = this.vals.get(i);
//		}
//		//padding
//		for(i=buffer.length-1;i>=(this.vals.size()+this.keys.size());i--) {
//			buffer[i]=-1;
//		}
//		return buffer;
//	}
//	
//	
//	public int[] to_meta_buffer() {
//		int[] buffer = new int[2];
//		buffer[0]=this.status;
//		buffer[1] = this.offset;
//		
//		return buffer;
//	}
//
//	
//}




