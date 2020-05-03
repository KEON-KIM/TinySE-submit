package edu.hanyang.submit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.MutableTriple;

import edu.hanyang.indexer.ExternalSort;

public class TinySEExternalSort implements ExternalSort {
	int prevStep = 0;
	int step = this.prevStep + 1;
	String infile;
	String outfile;
	String tmpdir;
	int blocksize;
	int nblocks;
	
	public TinySEExternalSort(String infile,
							String outfile,
							String tmpdir,
							int blocksize,
							int nblocks) {
		this.infile = infile;
		this.outfile = outfile;
		this.tmpdir = tmpdir;
		this.blocksize = blocksize;
		this.nblocks = nblocks;
	}
	public TinySEExternalSort() {
		this.infile = null;
		this.outfile = null;
		this.tmpdir = null;
		blocksize = 0;
		nblocks = 0;
	}
	
	
	public static void make_run_file(ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr, DataOutputStream os) throws IOException {
		for(MutableTriple<Integer, Integer, Integer> tmp : dataArr) {
			os.writeInt(tmp.getLeft());			
			os.writeInt(tmp.getMiddle());
			os.writeInt(tmp.getRight());
			os.flush();
		}
	}
	public static String make_dir(String tmdir, int paze) {
		String path = tmdir+File.separator+String.valueOf(paze);
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
	
	public static void init_run(String infile, 
								String tmpdir,
								int blocksize,
								int nblocks) throws IOException {
		
		int nElement = (blocksize*nblocks) / 12;
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(infile), blocksize)
				);
		
		ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
		boolean isEOF = false;
		DataManager dm = new DataManager(is);
		int run = 0;
		int paze = 0;
		String path = make_dir(tmpdir, paze);
		try {
			
			while(true) {
				
				MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
				dm.getTuple(ret);
				dataArr.add(ret);
				if((dataArr.size() == nElement ) || dm.isEOF) {
					DataOutputStream os = new DataOutputStream(
							new BufferedOutputStream(
									new FileOutputStream(path+File.separator+run+".data")));
					//sorting dataArr
					Collections.sort(dataArr);
					System.out.println(dataArr);
					//make_run_file(String tmpdir, dataArr, DataOutputStream os);
					make_run_file(dataArr, os);
					dataArr.clear();
//					System.out.println(dataArr);
					run++;
					continue;
				}
			}		
		}	
		catch (IOException e) {
			System.out.println(e);
		}	
		
	}
	public static void main(String[] args) throws IOException {
		/*
		String infile = "./test.data";
		String outfile = "./result.data";
		String tmpdir = "./tmpt/";
		int blocksize = 4096;
		int nblocks = 6;
		init_run(infile, tmpdir, blocksize, nblocks);
		*/
		
		
		String tmpDir = "./tmpt";
		int prevStep = 0;
		File[] fileArr = (new File(tmpDir + File.separator + String.valueOf(prevStep))).listFiles();
		System.out.println(fileArr.length);
		System.out.println(tmpDir + File.separator + String.valueOf(prevStep));
		System.out.println(fileArr[1].getAbsolutePath());
        
	}
	public void n_way_merge(List<DataInputStream> files, String outfile) throws IOException {
		PriorityQueue<DataManager> pq = new PriorityQueue<>(files.size(), new Comparator<DataManager>() {
			public int compare(DataManager o1, DataManager o2) {
				return o1.tuple.compareTo(o2.tuple);
			}
		});
		while(pq.size() != 0) {
			DataManager dm = pq.poll();
			MutableTriple<Integer, Integer, Integer> tmp = new MutableTriple<Integer, Integer, Integer>();
			dm.getTuple(tmp);
			//...
			
		}
		
	}
	public void _externalMergeSort(String tmpDir, String outfile, int step) throws IOException {
		File[] fileArr = (new File(tmpDir + File.separator + String.valueOf(this.prevStep))).listFiles();
		int cnt = 0;
		if (fileArr.length <= nblocks - 1) {
			for(File f : fileArr) {
				/*
				DataInputStream is = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(f.getAbsolutePath(), blocksize)
					);
				*/
			}
		}
		else {
			for(File f : fileArr) {
				/*
				 ...
				 cnt++;
				 if(cnt == nblocks -1) {
				 	n_way_merge(...);
				 }
				*/
			}
			
		}
	}
	//output buffer 변수 만드는거 전역변수
	
	public void sort(String infile, //input file path
					String outfile, //output file path
					String tmpdir, //temporary dir for
									// creating intermediate runs
					int blocksize, //4096 or 8192 bytes
					int nblocks) throws IOException { // available mem, block size, M

		init_run(infile, tmpdir, blocksize, nblocks);
		
		_externalMergeSort(tmpdir, outfile, step);

	}
	
	
}
class DataManager implements Comparable<DataManager>{
	public boolean isEOF = false;
	private DataInputStream dis = null;
	public MutableTriple<Integer,Integer,Integer> tuple = null;
	
	public DataManager(DataInputStream dis) throws IOException{
//		System.out.println("DataManager : 1"+dis);
		this.dis = dis;
		this.tuple = new MutableTriple<Integer,Integer,Integer>(dis.readInt(),dis.readInt(),dis.readInt());
//		readNext(dis); 
	};

	private boolean readNext() throws IOException {
		if(isEOF) return false;
//		System.out.println("DataManager : 4"+dis);
		tuple.setLeft(this.dis.readInt()); 
		tuple.setMiddle(this.dis.readInt()); 
		tuple.setRight(this.dis.readInt());
//		System.out.println("DataManager : 5"+dis);
		return true;
	}
	
	public void getTuple(MutableTriple<Integer,Integer,Integer> ret) throws IOException{
//		System.out.println("DataManager : 2"+dis);
		ret.setLeft(tuple.getLeft());
		ret.setMiddle(tuple.getMiddle()); 
		ret.setRight(tuple.getRight());
//		System.out.println(ret);
//		System.out.println("DataManager : 3"+dis);
		
		isEOF = (! this.readNext());
	}
	@Override
	public int compareTo(DataManager dm) {
		MutableTriple<Integer,Integer,Integer> tuple1 = this.tuple;
		MutableTriple<Integer,Integer,Integer> tuple2 = dm.tuple;
		
		if (tuple1.getLeft() < tuple2.getLeft()) return -1;
		else if (tuple1.getLeft() > tuple2.getLeft()) return 1;
		else {
			if(tuple1.getMiddle() < tuple2.getMiddle()) return -1;
			else if(tuple2.getMiddle() > tuple2.getMiddle()) return 1;
			else {
				if(tuple1.getRight() < tuple2.getRight()) return -1;
				else if(tuple1.getRight() > tuple2.getRight()) return 1;
				else return 0;
			}
		}
		
	}
	
}