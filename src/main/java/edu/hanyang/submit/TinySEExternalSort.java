/*
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

			_externalMergeSort(tmpdir, outfile, nblocks, blocksize);

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
		String path = make_dir(tmpdir, 0);
		
		
		int run = 0;
		int nElement = (blocksize*nblocks) / 12;
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(infile), blocksize)
				);
		ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
		DataManager dm = new DataManager(is);
		DataOutputStream os;
		
		while(!dm.isEOF) {
			MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
			
			dm.getTuple(ret);
			dataArr.add(ret);
			if((dataArr.size() == nElement )) {
				os = open_output_stream(path, run, blocksize);
				Collections.sort(dataArr);
				write_run_file(dataArr, os);
				dataArr.clear();
				os.close();
				run++;
			}
//			System.out.println(dm.av);
			
		}	
		os = open_output_stream(path, run, blocksize);
		Collections.sort(dataArr);
		write_run_file(dataArr, os);
		dataArr.clear();
		dm = null;
		os.close();
		is.close();	
	}
	
	public static void main(String[] args) throws IOException {
		
		
		String infile = "./test.data";
		String outfile = "./tmp/sorted.data";
		String outfile1 = "./tmp/0/11.data";
		String tmpdir = "./tmp/";
		int blocksize = 1024;
		int nblocks = 100;
		String chc = "./tmp/0/116.data";
		
		long timestamp = System.currentTimeMillis();
		init_run(infile, tmpdir, blocksize, nblocks);
		System.out.println("init run time duration: " + (System.currentTimeMillis() - timestamp));
		
		timestamp = System.currentTimeMillis();
		_externalMergeSort(tmpdir, outfile, nblocks, blocksize);
		System.out.println("external merge time duration: " + (System.currentTimeMillis() - timestamp));
		
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(outfile), blocksize)
				);
		DataManager dm = new DataManager(is);
		System.out.println(dm.ava);
	
	//	ReadFileByte(chc, blocksize);
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
		
		PriorityQueue<DataManager> pq = new PriorityQueue<>(files.size(), new Comparator<DataManager>() {
			public int compare(DataManager o1, DataManager o2) {
				return o1.tuple.compareTo(o2.tuple);
			}
		});
		
//		ArrayList<MutableTriple<Integer, Integer, Integer>> bufferArr = new ArrayList<>(blocksize);
		
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
		
		while(pq.size() != 0) {
			
			DataManager dm = pq.poll();
			MutableTriple<Integer, Integer, Integer> tmp = new MutableTriple<Integer, Integer, Integer>();
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
class DataManager implements Comparable<DataManager>{
	public boolean isEOF = false;
	private DataInputStream dis = null;
	public int ava;
	public MutableTriple<Integer,Integer,Integer> tuple = new MutableTriple<Integer,Integer,Integer>(0, 0, 0);
	
	public DataManager(DataInputStream dis) throws IOException{
		this.dis = dis;
		this.tuple = new MutableTriple<Integer,Integer,Integer>(this.dis.readInt(),this.dis.readInt(),this.dis.readInt());
		this.ava = dis.available();
	};

	private boolean readNext() throws IOException {
		if(this.dis.available() == 0) return false;
		
		this.tuple.setLeft(this.dis.readInt()); 
		this.tuple.setMiddle(this.dis.readInt()); 
		this.tuple.setRight(this.dis.readInt());
		this.ava -= 12;
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
*/
///*
package edu.hanyang.submit;

import java.io.IOException;

import edu.hanyang.indexer.ExternalSort;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import java.io.File;

public class TinySEExternalSort implements ExternalSort {
	
	public static void main(String[] args) throws IOException {
		
		
		String infile = "./test.data";
		String outfile = "./tmp/sorted.data";
		String outfile1 = "./tmp/0/11.data";
		String tmpdir = "./tmp/";
		int blocksize = 1024;
		int nblocks = 100;
		String chc = "./tmp/0/116.data";
		
		TinySEExternalSort ts = new TinySEExternalSort();
		
		ts.sort(infile, outfile, tmpdir, blocksize, nblocks);

		
		

		
	//	ReadFileByte(chc, blocksize);
	}
	
	public void sort(String infile, String outfile, String tmpdir, int blocksize, int nblocks) throws IOException {
		
		class Tuple{
			int index,word_id,doc_id,pos;
			public Tuple(int index, int word_id, int doc_id, int pos){
				this.index = index;
				this.word_id = word_id;
				this.doc_id = doc_id;
				this.pos = pos;
			}
		}
		
		class TripleSort implements Comparator<Triple<Integer,Integer,Integer>> {
			@Override 
			public int compare(Triple<Integer,Integer,Integer> a, Triple<Integer,Integer,Integer> b) { 
				if(a.getLeft() > b.getLeft()) return 1;
				else if(a.getLeft() < b.getLeft()) return -1;
				else{
					if(a.getMiddle() > b.getMiddle()) return 1;
					else if(a.getMiddle() < b.getMiddle()) return -1;
					else{
						if(a.getRight() > b.getRight()) return 1;
						else return -1;
					}
				} 
			} 
		}
		class TupleSort implements Comparator<Tuple> {
			@Override 
			public int compare(Tuple a, Tuple b) { 
				if(a.word_id > b.word_id) return 1;
				else if(a.word_id < b.word_id) return -1;
				else{
					if(a.doc_id > b.doc_id) return 1;
					else if(a.doc_id < b.doc_id) return -1;
					else{
						if(a.pos > b.pos) return 1;
						else return -1;
					}
				} 
			} 
		}
		long timestamp = System.currentTimeMillis();
		File dir = new File(tmpdir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(infile),blocksize));
		DataOutputStream run_writer;
		ArrayList<MutableTriple<Integer, Integer, Integer>> runs = new ArrayList<MutableTriple<Integer, Integer, Integer>>();
		int records = blocksize / ((Integer.SIZE/Byte.SIZE) * 3);
		int word_id, doc_id, pos;
		int run_cnt = 1;
		int run = records * 3 * (Integer.SIZE/Byte.SIZE) * nblocks; // 한 run의 용량
		int pass_cnt = 1;
		while(input.available() != 0){
			if( input.available() > run ) {
				while (runs.size() < nblocks * records){
					word_id = input.readInt();
					doc_id = input.readInt();
					pos = input.readInt();
					runs.add(MutableTriple.of(word_id,doc_id,pos));
				}
			} else {
				while (input.available() != 0){
					word_id = input.readInt();
					doc_id = input.readInt();
					pos = input.readInt();
					runs.add(MutableTriple.of(word_id,doc_id,pos));
				}
			}
			Collections.sort(runs, new TripleSort());
			run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
			for(MutableTriple<Integer,Integer,Integer> tuple : runs){
				run_writer.writeInt(tuple.getLeft());
				run_writer.writeInt(tuple.getMiddle());
				run_writer.writeInt(tuple.getRight());
			}
			run_writer.close();
			run_cnt++;
			runs.clear();
		}
		run_cnt--;
		input.close();
		// create run 완료
		System.out.println("init run time duration: " + (System.currentTimeMillis() - timestamp) );

		// merge pass 시작
		timestamp = System.currentTimeMillis();

		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		ArrayList<DataInputStream> run_reads = new ArrayList<DataInputStream>();
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile),blocksize));
		Tuple tuple;
		int index;
		while(true){
			int pre_runs = run_cnt;
			int cur_runs = pre_runs/nblocks;
			if(cur_runs*nblocks < pre_runs) cur_runs++;
			pass_cnt++;
			run_cnt = 1;
			int run_num = 1;
			int iter;
			for(int i=1; i<=cur_runs; i++){
				run_reads.clear();
				tuples.clear();
				if(pre_runs > nblocks){
					iter = nblocks;
					pre_runs -= nblocks;
				}
				else iter = pre_runs;
				for(int j=0; j<iter; j++){
					DataInputStream run_read = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/run_"+(pass_cnt-1)+"_"+run_num+".data"),blocksize));
					run_reads.add(run_read);
					run_num++;
				}
				if(cur_runs == 1) run_writer = output;
				else run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
				for(int j=0; j<iter; j++){
					word_id = run_reads.get(j).readInt();
					doc_id = run_reads.get(j).readInt();
					pos = run_reads.get(j).readInt();
					tuple = new Tuple(j,word_id,doc_id,pos);
					tuples.add(tuple);
				}
				while(true){
					Collections.sort(tuples, new TupleSort());
					tuple = tuples.get(0);
					index = tuple.index;
					run_writer.writeInt(tuple.word_id);
					run_writer.writeInt(tuple.doc_id);
					run_writer.writeInt(tuple.pos);
					tuples.remove(0);
					if(run_reads.get(index).available() > 0){
						word_id = run_reads.get(index).readInt();
						doc_id = run_reads.get(index).readInt();
						pos = run_reads.get(index).readInt();
						tuple = new Tuple(index,word_id,doc_id,pos);
						tuples.add(tuple);
						
					}
					if(tuples.isEmpty()) break;
				}
				run_cnt++;
				run_writer.close();
			}
			if(run_cnt == 2){
				break;
			}
			else{
				run_cnt--;
			}
		}
		System.out.println("time duration: " + (System.currentTimeMillis() - timestamp) );

//		merge pass 완료
		output.close();
	}
}
//*/