package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	
	static int blocksize;
	int nblocks;
	
	RandomAccessFile tree;
	RandomAccessFile meta;
	
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
		File treefile = new File("./tree.data");
		File metafile = new File("./meta.data");
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
	
	private static int[] readFromFile(RandomAccessFile file, int position, int size)
		throws IOException {
		file.seek(position * size);
		
		file.read();
		int[] integers = new int[size/Integer.BYTES - 1];
		
		for(int i = 0; i < integers.length; i++) {
			integers[i] = file.readInt();
//			System.out.print(integers[i]+",");
		}
//		System.out.println("]");
//		file.close();
		return integers;
	}
	
	private static int[] readFromMeta(RandomAccessFile file, int position, int size)
		throws IOException {
		
		file.seek(position*size);
		file.read();
		int[] integers = new int[size/Integer.BYTES];
		for(int i = 0; i < integers.length; i++) {
			integers[i] = file.readInt();
		}
//		file.close();
		return integers;
	}
	
	private static void writeToFile(RandomAccessFile file, int[] integers, int position, int size)
		throws IOException{
		
		file.seek(position*size); 
		for(int i : integers) {
			file.writeInt(i);
		}
//		file.close();
	}
	

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
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
		
		int i = 0;
		while(i < blocksize) {
			int key = is.readInt();
			int val = is.readInt();
			tree.insert(key, val);
			i++;
		}
		
		System.out.println(tree.cur.keys);
//		
//		int[] tree_buffer = readFromFile(tree.tree, tree.offset, tree.blocksize);
//		int[] meta_buffer = readFromMeta(tree.meta, tree.offset, 8);
//		
//		Node node = new Node(tree_buffer, meta_buffer, tree.blocksize);
//		System.out.println(node.keys);
		
	}
	@Override
	public void insert(int key, int val) throws IOException{
		
			
			
		
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
	public int search(int key) {
		// TODO Auto-generated method stub
		return 0;
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
	public static boolean isFull(Node node) {
		if(node.keys.size() == (blocksize-8)/(Integer.BYTES*2)) return true;
		return false;
	}
	
	
	
	public int[] splitLeafNode(Node node, String treepath, String metapath) throws IOException {
		int[] buffer;
		int fanout = (node.vals.size() / 2) + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer = node.to_tree_buffer(fanout);
		node.remove(fanout);
		writeToFile(this.tree, node.to_tree_buffer(), node.offset, blocksize);
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, this.offset, 8);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, 8);
		
		int[] piv = new int[2];
		piv[0] = brother.keys.get(0);
		piv[1] = this.offset;
		
		return piv;
	}
	
	public int[] splitNonLeafNode(Node node, String treepath, String metapath) throws IOException{
		int[] buffer;
		int[] piv = new int[2];
		int fanout = (node.vals.size() / 2) + 1;
		/* cur node 작업
		 * leaf node이므로 metafile 작업 x*/
		buffer = node.to_tree_buffer(fanout+1);
		piv[0] = node.keys.get(fanout);
		piv[1] = node.vals.get(fanout);
		node.remove(fanout);
		writeToFile(this.tree, node.to_tree_buffer(), node.offset, blocksize);
		writeToFile(this.meta, node.to_meta_buffer(), node.offset, 8);
		
		this.offset++;
		Node brother = new Node(buffer, blocksize, this.offset, 8);
		writeToFile(this.tree, brother.to_tree_buffer(), brother.offset, blocksize);
		writeToFile(this.meta, brother.to_meta_buffer(), brother.offset, 8);
		
		return piv;
	}
	
	//status, offset 순으로 저장함
	
	//leaf 찾기
	public Node search_leaf_node(int key) throws IOException {
		Node parent = get_root();
		cache.add(parent);
		Node child = get_child(parent, key);
		cache.add(child);
		
		while(child.status == 2) {
			parent = child;
			child = get_child(parent, key);
			cache.add(child);
		}
		
		return child;
	}
	public Node get_child(Node node, int key) throws IOException {
		if(node.status == 2) {
			System.out.println("this node is leafnode : ");
			return null;
		}
		
		int point = node.get_point(key);
		int[] tree_buffer = readFromFile(this.tree, point, blocksize);
		int[] meta_buffer = readFromMeta(this.meta, point, 8);
		Node child = new Node(tree_buffer, meta_buffer, this.blocksize);
		
		return child;
	}
	public Node get_root() throws IOException {
		int[] tree_buffer = readFromFile(this.tree, 0, this.blocksize);
		int[] meta_buffer = readFromMeta(this.meta, 0, 8);
		
		Node root = new Node(tree_buffer, meta_buffer, this.blocksize);
		
		return root;
	}

}

class Node {
	int offset;
	
	/* status 
	 * 0 : root
	 * 1 : non leaf
	 * 2 : leaf
	 */
	int status; 
	int max_keys;
	
	List<Integer> keys;
	List<Integer> vals;
	
	
	/*생성자는 2가지
	 * stream을 input으로 받을때 <- search 할때 필요
	 * 그냥 크기만큼 key, val을 생성 <- 새로운 노드를 만들때 필요 ex) split, write
	 */
	
	//커서 만들기.
	Node(int[] tree_buffer, int blocksize, int offset, int status) {
//		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
		int i;

		for(i = 0; i < tree_buffer.length / 2; i ++) {
			if(tree_buffer[i*2+1]==0||tree_buffer[i*2]==0) break;
			vals.add(tree_buffer[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
			keys.add(tree_buffer[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
		}
		if(tree_buffer[i*2]!=0) vals.add(tree_buffer[i*2]);
	}
	
	Node(int blocksize, int offset, int status) {
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
	}
	
	Node(int[] tree_buffer, int[] meta_buffer, int blocksize) {
//		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.status = meta_buffer[0];
		this.offset = meta_buffer[1];
		int i;
		/**/
		for(i = 0; i < tree_buffer.length / 2; i ++) {
			if(tree_buffer[i*2]==-1||tree_buffer[i*2+1]==-1) {
			}
			else {
				vals.add(tree_buffer[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
				keys.add(tree_buffer[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
			}
		}
		vals.add(tree_buffer[i/2]);
		
	}
	
	Node(int[] meta_buffer, int blocksize) {
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.status = meta_buffer[0];
		this.offset = meta_buffer[1];
	}
	
	public boolean isFull() {
		if(keys.size() == this.max_keys) return true;
		
		return false;
	}
	
	/*
	 * insert를 하기전에 full인지 아닌지부터 check*/
	public void insert(int key, int val) {
		if (this.keys.size() == 0) {
			this.keys.add(key);
			this.vals.add(val);
			return ;
		}
		Iterator<Integer> it = this.keys.iterator();
		
			
		while(it.hasNext()) {
			int n = it.next();
			
			if(n > key) {
				
				this.keys.add(this.keys.indexOf(n), key);
				this.vals.add(this.keys.indexOf(n), val);
				return;
			}
		}
		this.keys.add(key);
		this.vals.add(val);
	}
	
	
	/*
	 * keys, vals를 int 배열 buffer로 만들기*/
	public int[] to_tree_buffer() {
		int num_buffer = max_keys * 2 + 1;
		int[] buffer = new int[num_buffer];
		for(int i = 0; i < this.keys.size(); i++) {
			buffer[i*2] = this.vals.get(i);
			buffer[i*2 + 1] = this.keys.get(i);
		}
		buffer[buffer.length - 1] = this.vals.get(vals.size()-1);
		
		return buffer;
	}
	public int[] to_tree_buffer(int index) {
		int num_buffer = max_keys * 2 + 1;
		int[] buffer = new int[num_buffer];
		for(int i = 0; i < this.keys.size() - index ; i++) {
			buffer[i*2] = this.vals.get(index + i);
			buffer[i*2 + 1] = this.keys.get(index + i);
		}
		buffer[buffer.length - 2*index - 1] = this.vals.get(vals.size() - 1);
		return buffer;
	}
	public int[] to_meta_buffer() {
		int[] buffer = new int[2];
		buffer[0] = this.status;
		buffer[1] = this.offset;
		
		return buffer;
	}
	/* index부터 keys, vals remove*/
	public void remove(int index) {
		int num_keys = this.keys.size();
		for(int i = 0; i < num_keys - index; i++) {
			this.keys.remove(index);
			this.vals.remove(index);
		}
	}
	
	public int get_point(int key){ 
		Iterator<Integer> it = this.keys.iterator();
		while(it.hasNext()) {
			int n = it.next();
			if(n > key) {
				return this.vals.get(this.keys.indexOf(n));
			}
		}
		
		return this.vals.get(vals.size() - 1);
	}
	
	
	
}





