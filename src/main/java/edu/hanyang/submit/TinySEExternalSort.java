
package edu.hanyang.submit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
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
<<<<<<< HEAD
			
			Runtime.getRuntime().gc();
=======
>>>>>>> ebbedf3c20180bb0f2e2778ea6631af4f9ed5287
			init_run(infile, tmpdir, blocksize, nblocks);

			
			_externalMergeSort(tmpdir, outfile, nblocks, blocksize);

	}
	
	/*
	 * write ArrayList's file using DataOutputStream
	 */
	public static void write_run_file(ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr, DataOutputStream os) throws IOException {
		for(MutableTriple<Integer, Integer, Integer> tmp : dataArr) {
			os.writeInt(tmp.getLeft());			
			os.writeInt(tmp.getMiddle());
			os.writeInt(tmp.getRight());
		}
	}
	
	/*
	 * make directory in each step to tmpdir
	 */
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
	
	/*
	 * make tmpdir
	 * if already exist, ignore
	 */
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
	
	/*
	 * open DataOutputStream to path/'run num'.data
	 */
	public static DataOutputStream open_output_stream(String path, int run, int blocksize) throws IOException {
		return new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path+File.separator+run+".data"), blocksize));
	}
	
	
	
	/*
	 * create init run files
	 */

	public static void init_run(String infile, 
			String tmpdir,
			int blocksize,
			int nblocks) throws IOException {
		nblocks /= 10;
		
		make_tmp(tmpdir); //make tmpdir
		String path = make_dir(tmpdir, 0); //make tmpdir/0/ directory and return path

		//open DataInputStream to infile
		
		DataManager dm = new DataManager( new DataInputStream(
											new BufferedInputStream(
												new FileInputStream(infile), blocksize)
													));
		
		//declare DataOutputStream
		DataOutputStream os;


		int run = 0; //run file number
		int membyte = nblocks*blocksize; // whole byte that using for store files
		int nElement = (nblocks * blocksize) / 12 ; // numbers that can contain tuple elements
		
		MutableTriple<Integer, Integer, Integer> tuple = new MutableTriple<>();
		int p = dm.dis.available() / membyte;
		
		
		
		for(int i = 0; i < p; i++) { //repeat p times
			ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
			for(int j = 0; j < nElement; j++) {
				dm.getTuple(tuple);
				dataArr.add(tuple);
				tuple = new MutableTriple<>();
				
			}
			Collections.sort(dataArr);
			os = open_output_stream(path, run, blocksize); //open outputstream to path
			write_run_file(dataArr, os); // write
			
			
			dataArr.clear();
			os.close();
			run++;
		}
		
		ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
		while(dm.dis.available() != 0)	 {
			dm.getTuple(tuple);
			dataArr.add(tuple);
			tuple = new MutableTriple<>();
		}
		Collections.sort(dataArr);
		os = open_output_stream(path, run, blocksize); //open outputstream to path
		write_run_file(dataArr, os); // write
		
		dataArr.clear();
		os.close();
		dm.dis.close();


	}
	

//	
//	public static void main(String[] args) throws IOException {
//		
//		
//		System.gc();
//		
//		
//		String infile = "./test2.data";
//		String outfile = "./tmp/sorted.data";
//		String tmpdir = "./tmp/";
//		int blocksize = 4096;
//		int nblockss;
//		int nblocks = 1000;
//		System.out.println("	init				externerl");
//		
//		clean("./tmp");
//		Runtime.getRuntime().gc();
//		long timestamp = System.currentTimeMillis();
//		
//		init_run(infile, tmpdir, blocksize, nblocks);
//		
//		long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		System.out.printf("blocksize : %d, nblocks : %d 일때\n", blocksize, nblocks);
//		System.out.println("init run time duration: " + (System.currentTimeMillis() - timestamp));
//		long init_t = System.currentTimeMillis() - timestamp;
//		
//		Runtime.getRuntime().gc();
//		
//		timestamp = System.currentTimeMillis();
//		_externalMergeSort(tmpdir, outfile, nblocks, blocksize);
//		used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		System.out.println("external merge time duration: " + (System.currentTimeMillis() - timestamp));
//		System.out.println("used memory is " + (used/1024)/1024 + " MB");
//		
//		System.out.println();
//		
//		
//		
//		
//
//	
//	
//		//ReadFileByte(outfile, blocksize);
//	}
//	public void init() {
//		clean("./tmp");
//		File resultFile = new File("./sorted.data");
//		if(resultFile.exists()) {
//			resultFile.delete();
//		}
//	}
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
	
	//count and see the .data file
//	public static void ReadFileByte(String outfile, int blocksize) throws IOException {
//		int count=0;
//		
//		DataInputStream is = new DataInputStream(
//				new BufferedInputStream(
//					new FileInputStream(outfile), blocksize)
//				);
//		DataManager dm = new DataManager(is);
//		
//		while(!dm.isEOF) {
//				MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
//				count++;
//				dm.getTuple(ret);
//				System.out.println(ret);
//		}
//	
//		System.out.println(count);
//			
//		
//	}
	
	
	/*
	 * Merge nblocks-1 files
	 */
	public static void n_way_merge(List<DataInputStream> files, String tmpdir, int run, int step, int blocksize) throws IOException {
		
		PriorityQueue<DataManager> pq = new PriorityQueue<>(files.size(), new Comparator<DataManager>() {
			public int compare(DataManager o1, DataManager o2) {
				return o1.tuple.compareTo(o2.tuple);
			}
		});
		
		
		String path = make_dir(tmpdir, step);
		
		
		for(DataInputStream dis : files) {
			DataManager dm = new DataManager(dis);
			pq.offer(dm);
			
		}
		DataOutputStream os;
		
		
		if (run == -1) {
			os = new DataOutputStream(
					new BufferedOutputStream(
						new FileOutputStream(tmpdir), blocksize));
		} else {
			os = open_output_stream(path, run, blocksize);
		}
		
		MutableTriple<Integer, Integer, Integer> tmp = new MutableTriple<Integer, Integer, Integer>();
		
		
		
		while(pq.size() != 0) {
			DataManager dm = pq.poll();
			
			dm.getTuple(tmp);
			
			os.writeInt(tmp.left);
			os.writeInt(tmp.middle);
			os.writeInt(tmp.right);
			if(dm.isEOF) {
				continue;
			} 
			pq.add(dm);
			
			
		}
		pq.clear();
		os.close();
		files.clear();
		
	}
	
	public static void _externalMergeSort(String tmpdir, String outfile, int nblocks, int blocksize) throws IOException {		
		File[] fileArr = (new File(tmpdir + File.separator + String.valueOf(prevStep))).listFiles();
		List<DataInputStream> files = new ArrayList<>();
		int run = 0;
//		nblocks /= 10;
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
			for(DataInputStream dis : files) {
				dis.close();
			}
			files.clear();
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
			for(DataInputStream dis : files) {
				dis.close();
			}
			files.clear();
			prevStep++; step++;
			_externalMergeSort(tmpdir, outfile, nblocks, blocksize);
		} 
	}
}
class DataManager implements Comparable<DataManager>, Cloneable {
	public boolean isEOF = false;
	public DataInputStream dis = null;
	public MutableTriple<Integer,Integer,Integer> tuple = new MutableTriple<Integer,Integer,Integer>(0, 0, 0);
	
	public DataManager(DataInputStream dis) throws IOException{
		this.dis = dis;
		this.tuple = new MutableTriple<Integer,Integer,Integer>(this.dis.readInt(),this.dis.readInt(),this.dis.readInt());
	};

	private boolean readNext() throws IOException {
		try {
		this.tuple.setLeft(this.dis.readInt()); 
		} catch(IOException e) {
			return false;
		}
		this.tuple.setMiddle(this.dis.readInt()); 
		this.tuple.setRight(this.dis.readInt());
		return true;
		
	}
	
	public void getTuple(MutableTriple<Integer,Integer,Integer> ret) throws IOException{
		ret.setLeft(this.tuple.getLeft());
		ret.setMiddle(this.tuple.getMiddle()); 
		ret.setRight(this.tuple.getRight());
		
		isEOF = (!this.readNext());
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

