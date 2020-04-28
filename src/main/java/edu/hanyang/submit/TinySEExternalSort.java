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
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.MutableTriple;

import edu.hanyang.indexer.ExternalSort;

public class TinySEExternalSort implements ExternalSort {
	public static void copyDataStream() throws IOException{ 
		
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream("./test.data"), 1024)
				);
		DataOutputStream os = new DataOutputStream(
				new BufferedOutputStream(
					new FileOutputStream("./testcopy.data"), 1024)
				);	
		//List<String> results = new List<String>();
		ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>();

		File file = new File("./test.data");
		int len = (int)file.length();
		int cnt = 0;
//		DataManager manager = new DataManager(is);
//		dataArr.add(manager.tuple);

		try {
			for (int i = 0; i< len; i++) {
				DataManager manager = new DataManager(is);
				dataArr.add(manager.tuple);
				System.out.println(dataArr.get(i));
				cnt++;
				
			}		
			
		}
		catch (Exception e) {
			System.out.println("counting : "+cnt);		
		}
		
	
		//String tmpDir = "./test.data";
		//File[] fileArr = (new File(tmpDir+File.separator + String.valueOf(prevStep))).listFiles();
//		for (int i = 0; i< len-1; i++) {
//			
//		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Starts");
		copyDataStream();
		System.out.println("Clear");
	}
	
	//output buffer 변수 만드는거 전역변수
	
	public void sort(String infile, //input file path
					String outfile, //output file path
					String tmpdir, //temporary dir for
									// creating intermediate runs
					int blocksize, //4096 or 8192 bytes
					int nblocks) throws IOException { // available mem, block size, M
		// complete the method
		
		// 1. initial phase
		// ArrayList<MutableTriple<Integer,Integer,Integer>> dataArr = new ArrayList<>();
		// 2. n_way merge
		// _externalMergeSort(tmpdir,outfile,0);
		
		
		//1. infile i/o 열기
		//   (term, docID, pos)로 구성
		//2. infile의 내용을 최대한 읽을수있을만큼 많이 읽어들여서 quicksort 써서 정렬하고 run에 저장
		//3. 각 run에서 M-1개씩 한꺼번에 merge
		//4. output buffer memory준비(Memory one block)
		//5. M-1 개의 runs block들 에서 가장첫번째 요소 비교후 가장큰 block을 output buffer에 저장 
		//6. if outbuffer 꽉차면 재일앞에꺼 push to disk and memory 비우기 
		

		// infile i/o 열기, blocksize 만큼씩
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(infile), blocksize));
		
		// 최종 저장될 outfile
		DataOutputStream os = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(outfile), blocksize));
		
		File file = new File(infile);
		int len = (int)file.length();
		
		try {
			for(int i = 0; i < len; i++) {
				os.write(is.readByte());
			}
		}catch(IOException e) {
			e.printStackTrace();
		}

	}
	public void merge(int i, int j) { //인자는 run, priority queue 써서 각 run cur 제일 우선순위 output mem, 모든 input buffer merge될때까지 반복
		 
	}
	
	public void externalmergesort(int Paze) {
		//현재 merge 해야하는 runs의 갯수(N)가 M-1보다 작은지 check
		//if N < M-1 
		// 작거나 같으면 이번 페이즈가 끝이므로 call merge()
		//if N>= M 페이즈 수가 많다
		//M-1개의 runs를 머지한(0~M-2,M-1~2(M-1)-1) 
		//다하면 call externalmergesort
		//pass의 갯수 log_(M-1)^N
	}
	
}
class DataManager {
	public boolean isEOF = false;
	private DataInputStream dis = null;
	public MutableTriple<Integer,Integer,Integer> tuple = new MutableTriple<Integer,Integer,Integer>(0,0,0);
	public DataManager(DataInputStream dis) throws IOException{
//		System.out.println("DataManager : 1"+dis);
		readNext(dis);
	};

	private boolean readNext(DataInputStream dis) throws IOException {
		if(isEOF) return false;
//		System.out.println("DataManager : 4"+dis);
		tuple.setLeft(dis.readInt()); tuple.setMiddle(dis.readInt()); tuple.setRight(dis.readInt());
//		System.out.println("DataManager : 5"+dis);
		return true;
	}
	
	public void getTuple(MutableTriple<Integer,Integer,Integer> ret,DataInputStream dis) throws IOException{
//		System.out.println("DataManager : 2"+dis);
		ret.setLeft(tuple.getLeft());ret.setMiddle(tuple.getMiddle()); ret.setRight(tuple.getRight());
//		System.out.println(ret);
//		System.out.println("DataManager : 3"+dis);
		
		isEOF = (! readNext(dis));
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