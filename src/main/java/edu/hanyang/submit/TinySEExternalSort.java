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
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.MutableTriple;

import edu.hanyang.indexer.ExternalSort;

public class TinySEExternalSort implements ExternalSort {
	
	public static void make_run_file(ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr, DataOutputStream os) throws IOException {
		for(MutableTriple<Integer, Integer, Integer> tmp : dataArr) {
			os.writeInt(tmp.getLeft());			
			os.writeInt(tmp.getMiddle());
			os.writeInt(tmp.getRight());
			os.flush();
		}
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
		
		
		int run = 0;
		try {
			
			while(true) {
				DataManager dm = new DataManager(is);
				MutableTriple<Integer, Integer, Integer> ret = new MutableTriple<Integer, Integer, Integer>();
				dm.getTuple(ret);
				dataArr.add(ret);
				
				if((dataArr.size() == nElement ) || (dm.isEOF == true)) {
					DataOutputStream os = new DataOutputStream(
							new BufferedOutputStream(
									new FileOutputStream("./tmpt/run0"+run+".data")));
					//sorting dataArr
					Collections.sort(dataArr);
					//make_run_file(String tmpdir, dataArr, DataOutputStream os);
					make_run_file(dataArr, os);
					dataArr.clear();
//					System.out.println(dataArr);
					run++;
				}	
			}		
		}	
		catch (IOException e) {
			System.out.println(e);
		}	
		
	}
	public static void main(String[] args) throws IOException {
		
		
	}
	
	//output buffer 변수 만드는거 전역변수
	
	public void sort(String infile, //input file path
					String outfile, //output file path
					String tmpdir, //temporary dir for
									// creating intermediate runs
					int blocksize, //4096 or 8192 bytes
					int nblocks) throws IOException { // available mem, block size, M

		init_run(infile, tmpdir, blocksize, nblocks);

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
		tuple.setLeft(dis.readInt()); 
		tuple.setMiddle(dis.readInt()); 
		tuple.setRight(dis.readInt());
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

class QuickSorter {
	public void fuck(int a) {
		System.out.println("Fucking");
	}
    public static List<Integer> quickSort(List<Integer> list) {
        if (list.size() <= 1) return list;
        int pivot = list.get(list.size() / 2);

        List<Integer> lesserArr = new LinkedList<>();
        List<Integer> equalArr = new LinkedList<>();
        List<Integer> greaterArr = new LinkedList<>();

        for (int num : list) {
            if (num < pivot) lesserArr.add(num);
            else if (num > pivot) greaterArr.add(num);
            else equalArr.add(num);
        }

        return Stream.of(quickSort(lesserArr), equalArr, quickSort(greaterArr))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}