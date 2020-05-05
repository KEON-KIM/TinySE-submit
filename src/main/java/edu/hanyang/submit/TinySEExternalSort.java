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
	static int prevStep = 0;
	static int step = 1;
	
	public void sort(String infile, //input file path
			String outfile, //output file path
			String tmpdir, //temporary dir for
							// creating intermediate runs
			int blocksize, //4096 or 8192 bytes
			int nblocks) throws IOException { // available mem, block size, M

			init_run(infile, tmpdir, blocksize, nblocks);

			_externalMergeSort(tmpdir, outfile, step, nblocks, blocksize);

	}
	public static void write_run_file(ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr, DataOutputStream os) throws IOException {
		for(MutableTriple<Integer, Integer, Integer> tmp : dataArr) {
			os.writeInt(tmp.getLeft());			
			os.writeInt(tmp.getMiddle());
			os.writeInt(tmp.getRight());
			os.flush();
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
	public static DataOutputStream open_output_stream(String path, int run, int blocksize) throws IOException {
		return new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path+File.separator+run+".data"), blocksize));
	}
	public static void init_run(String infile, 
								String tmpdir,
								int blocksize,
								int nblocks) throws IOException {
		
		
		make_tmp(tmpdir);
		
		int run = 0;
		int paze = 0;
		String path = make_dir(tmpdir, paze);
		int nElement = (blocksize*nblocks) / 12;
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(infile), blocksize)
				);
		ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
		DataManager dm = new DataManager(is);
		
		try {
			while(true) {
				MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
				dm.getTuple(ret);
				dataArr.add(ret);
				if((dataArr.size() == nElement )) {
					DataOutputStream os = open_output_stream(path, run, blocksize);
					Collections.sort(dataArr);
					write_run_file(dataArr, os);
					dataArr.clear();
					os.close();
					run++;
				}
			}		
		}	
		catch (IOException e) {
			DataOutputStream os = open_output_stream(path, run, blocksize);
			Collections.sort(dataArr);
			write_run_file(dataArr, os);
			dataArr.clear();
			os.close();
			is.close();
		}	
		
		
	}
	public static void main(String[] args) throws IOException {
		
		
		String infile = "./test.data";
		String outfile = "./tmp/sorted.data";
		String outfile1 = "./tmp/0/11.data";
		String tmpdir = "./tmp/";
		int blocksize = 1024;
		int nblocks = 1000;
		String chc = "./tmp/0/10.data";
 		ReadFileByte(chc, blocksize);
		/*
		long timestamp = System.currentTimeMillis();
		init_run(infile, tmpdir, blocksize, nblocks);
		_externalMergeSort(tmpdir, outfile, step, nblocks, blocksize);
		System.out.println("time duration: " + (System.currentTimeMillis() - timestamp) + " msecs with " + nblocks + " blocks of size " + blocksize + " bytes");
		*/

	}
	public static void ReadFileByte(String outfile, int blocksize) {
		int count=0;
		try {
			DataInputStream is = new DataInputStream(
					new BufferedInputStream(
						new FileInputStream(outfile), blocksize)
					);
			DataManager dm = new DataManager(is);
		
			while(true) {
					MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
					count++;
					dm.getTuple(ret);
					System.out.println(ret);
			}
		}catch(IOException e) {
			System.out.println(count);
			
		}
	}
	public static void n_way_merge(List<DataInputStream> files, String tmpdir, int run, int step, int blocksize) throws IOException {
		
		PriorityQueue<DataManager> pq = new PriorityQueue<>(files.size()-1, new Comparator<DataManager>() {
			public int compare(DataManager o1, DataManager o2) {
				return o1.tuple.compareTo(o2.tuple);
			}
		});
		
		ArrayList<MutableTriple<Integer, Integer, Integer>> bufferArr = new ArrayList<>(blocksize);
		
		String path = make_dir(tmpdir, step);
		
		DataManager[] dmArr = new DataManager[files.size()];
		int i = 0;
		
		for(DataInputStream dis : files) {
			dmArr[i] = new DataManager(dis);
			pq.offer(dmArr[i]);
			i++;
		}
		DataOutputStream os;
		
		
		if (run == -1) {
			os = new DataOutputStream(
					new BufferedOutputStream(
						new FileOutputStream(tmpdir), blocksize));
		} else {
			os = open_output_stream(path, run, blocksize);
		}
		
		while(pq.size() != 0) {
			try {
				DataManager dm = pq.poll();
				MutableTriple<Integer, Integer, Integer> tmp = new MutableTriple<Integer, Integer, Integer>();
				dm.getTuple(tmp);
				bufferArr.add(tmp);
				if(bufferArr.size() == blocksize) {
					write_run_file(bufferArr, os);
					bufferArr.clear();
				}
				if(dm.isEOF) {
					continue;
				}
				pq.add(dm);
			} catch(IOException e) {}
			
		}
		write_run_file(bufferArr, os);
		bufferArr.clear();
		os.close();
		files.clear();
		
	}
	
	public static void _externalMergeSort(String tmpdir, String outfile, int step, int nblocks, int blocksize) throws IOException {		
		File[] fileArr = (new File(tmpdir + File.separator + String.valueOf(prevStep))).listFiles();
		List<DataInputStream> files = new ArrayList<>();
		int run = 0;
		
		if (fileArr.length <= nblocks - 1) {
			run = -1;
			for(File f : fileArr) {
				files.add(new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(f), blocksize)
						));
			}
			n_way_merge(files, outfile, run, step, blocksize);
			fileArr = null;
		}
		else {
			int cnt = 0;
			for(File f : fileArr) {
				files.add(new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(f), blocksize)
						));
				cnt++;
				if(cnt == nblocks-1) {
					n_way_merge(files, tmpdir, run, step, blocksize);
					cnt = 0;
					files.clear();
					run++;
				}
				
			}
			n_way_merge(files, tmpdir, run, step, blocksize);
			prevStep++; step++;
			_externalMergeSort(tmpdir, outfile, step, nblocks, blocksize);
		} 
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