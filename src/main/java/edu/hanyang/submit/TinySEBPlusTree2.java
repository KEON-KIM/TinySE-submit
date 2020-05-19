package edu.hanyang.submit;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutableTriple;

import edu.hanyang.indexer.BPlusTree;
import junit.framework.Test;

public class TinySEBPlusTree2 implements BPlusTree{
	public List<NodeManager> rootlist = new ArrayList<>();
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(int arg0, int arg1) {
		if (rootlist.size()==0) {
			NodeManager nm = new NodeManager(arg0, arg1);
			rootlist.add(nm);
			// TODO Auto-generated method stub
		}
		else {
			List<Integer> tmp = rootlist.get(0).getList();
			if(arg0<tmp.get(0)) {
				List<NodeManager> childlist = new ArrayList<>();
				NodeManager nm = new NodeManager(arg0, arg1);
				childlist.add(nm);
				rootlist.get(0).tuple.setLeft(childlist);
			}
			else {
				List<NodeManager> childlist = new ArrayList<>();
				NodeManager nm = new NodeManager(arg0, arg1);
				childlist.add(nm);
				rootlist.get(0).tuple.setRight(childlist);
			}
		}
		
		
	}

	@Override
	public void open(String arg0, String arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int search(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) throws IOException {
		String metapath = "./tmp/bplustree.meta";
		String savepath = "./tmp/bplustree.tree";
		int blocksize = 52;
		int nblocks = 10;
 
		File treefile = new File(savepath);
		//Delete Code
		if (treefile.exists()) {
			if (! treefile.delete()) {
				System.err.println("error: cannot remove files");
				System.exit(1);
			}
		}
		//Main_Tree Code
		TinySEBPlusTree2 tree = new TinySEBPlusTree2();
		tree.open(metapath, savepath, blocksize, nblocks);
		tree.insert(5, 10);
		tree.insert(6, 15);
		tree.insert(4, 20);
		System.out.println(tree.rootlist.get(0).tuple.getLeft());
		System.out.println(tree.rootlist.get(0).tuple.getMiddle());
		System.out.println(tree.rootlist.get(0).tuple.getRight());
//		System.out.println(tree.search(5));
//		System.out.println(tree.search(6));
//		System.out.println(tree.search(4));
	}
}

class NodeManager{
	public boolean isEOL = false;
	public MutableTriple<Object,List,Object> tuple = new MutableTriple<Object,List,Object>();
	//integer1 : Position(Object), integer2 : KeyValue, integer3: Value or(Object???)
	public NodeManager(int args0, int args1){
		 //실제 메모리 주소가 아닌 메모리 주소값을 해싱된 값
		List<Integer> DoubleList = new ArrayList<Integer>(0);
		DoubleList.add(args0);
		DoubleList.add(args1);
		tuple.setMiddle(DoubleList);
		System.out.println(tuple.getMiddle().get(0));
		
//		System.out.println(DoubleList.get(0));
//		tuple.setMiddle(args0);
//		tuple.setRight(args1);
		
	}

	private boolean readNext() throws IOException {
		return true;
	}
	
	public List getList(){
		return tuple.getMiddle();
	}
	
}

