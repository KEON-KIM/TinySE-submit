package edu.hanyang;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.hanyang.submit.TinySEExternalSort;

@Ignore("Delete this line to unit test stage 2")
public class ExternalSortTest {
	@Before
	public void init() {
		clean("./tmp");
		File resultFile = new File("./sorted.data");
		if(resultFile.exists()) {
			resultFile.delete();
		}
	}
	
	@Test
	public void TestSort() throws IOException {
		int blocksize = 4096;
		int nblocks = 1000;
		ClassLoader classLoader = this.getClass().getClassLoader();
		File infile = new File(classLoader.getResource("test2.data").getFile());
		String outfile = "./tmp/sorted.data";
		String tmpdir = "./tmp";
		File resultFile = new File(outfile);
		
		TinySEExternalSort sort = new TinySEExternalSort();
		long timestamp = System.currentTimeMillis();
		sort.sort(infile.getAbsolutePath(), outfile, tmpdir, blocksize, nblocks);
		System.out.println("time duration: " + (System.currentTimeMillis() - timestamp) + " msecs with " + nblocks + " blocks of size " + blocksize + " bytes");

		
		File answerFile = new File(classLoader.getResource("answer2.data").getFile());
		DataInputStream resultInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(resultFile)));
		DataInputStream answerInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(answerFile)));

		assertNotNull(resultInputStream);
		assertNotNull(answerInputStream);

		for (int i = 0; i < 100000; i++) {
			assertEquals(resultInputStream.readInt(), answerInputStream.readInt());
			assertEquals(resultInputStream.readInt(), answerInputStream.readInt());
			assertEquals(resultInputStream.readInt(), answerInputStream.readInt());
		}

		resultInputStream.close();
		answerInputStream.close();
	}

	private void clean(String dir) {
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
}












/*
public class TinySEExternalSort implements ExternalSort {


    public void sort(String infile, String outfile, String tmpDir, int blocksize, int nblocks) throws IOException {

        // 1) initial phase
    	long timestamp = System.currentTimeMillis();
        // 메모리에 올릴 수 있는 전체 사이즈 = blocksize * nblocks (= M) / Integer.SIZE
        int nElement = blocksize * nblocks / Integer.SIZE / 3;
        //tmp 폴더 생성
        File fileTmpDir = new File(tmpDir);
        if(!fileTmpDir.exists()){
            fileTmpDir.mkdir();
        }
        //initial run의 개수
        int number_of_initial_run=0;
        //File I/O와 같은 것들을 처리할 DataManager 선언
        
        DataManager dm = new DataManager();
        dm.openDis(infile, blocksize);
        //정렬해야하는 input data를 읽을 수 있을 때까지 읽음
        while(dm.dis.available()!=0){
            ArrayList<MutableTriple<Integer, Integer, Integer>> dataArr = new ArrayList<>(nElement);
            //사용 할 수 있는 메모리 크기에 따른, 메모리에 올릴 수 있는 element의 수 = nElement 만큼 데이터를 읽어 들임
            for(int i=0; i<nElement; i++){
                try{
                    MutableTriple<Integer, Integer, Integer> tuple = new MutableTriple<>();
                    dm.readNext();
                    dm.getTuple(tuple);
                    dataArr.add(tuple);
                }catch (EOFException e) {
                    break;
                }
            }
            dataArr.sort(null);
            String runDir = tmpDir+File.separator+"pass0";
            File fileRunDir = new File(runDir);
            if(!fileRunDir.exists()){
                fileRunDir.mkdir();
            }
            String initial_run = runDir+File.separator+"run"+ number_of_initial_run +".data";
            File fileInitialRun = new File(initial_run);
            //initial run 생성하기
            fileInitialRun.createNewFile();
            dm.openDos(initial_run, blocksize);
            for (MutableTriple<Integer, Integer, Integer> curData : dataArr) {
                try{
                    dm.writeNext(curData);
                }catch (EOFException e) {
                    break;
                }
            }
            number_of_initial_run++;
            dm.closeDos();
        }
        // File cursor 닫기
        dm.closeDis();
        System.out.println("init time: " + (System.currentTimeMillis() - timestamp));
        // initial run 생성해서 quick sort 정렬까지 완료! 2) n-way merge만 구현하면 됨!
        // 2) n-way merge
        timestamp = System.currentTimeMillis();
        _externalMergeSort(tmpDir, outfile, nblocks, blocksize, 1);
        System.out.println("merge time: " + (System.currentTimeMillis() - timestamp));
        System.out.println("External Merge Sort Done");
        System.out.println();
    }

    private void _externalMergeSort(String tmpDir, String outputFile, int nblocks, int blocksize, int step) throws IOException{
        String prevStep = "pass" + (step-1);
        File[] fileArr = (new File(tmpDir + File.separator + String.valueOf(prevStep))).listFiles();
        ArrayList<File> fileList = new ArrayList<>();
        int number_of_run=0;
        String passDirpath = tmpDir + File.separator + "pass" + step;
        File passDir = new File(passDirpath);
        if (!passDir.exists()) {
            passDir.mkdir();
        }

        System.out.println();
        System.out.println("====================================================");
        System.out.println("step" + step + " # of runs = " + fileArr.length);
        System.out.println("nblocks -1 = " + (nblocks-1));
        System.out.println();
        

        if(fileArr.length <= nblocks - 1){
           
           for(File f : fileArr) {
               fileList.add(f);
            }
           
           System.out.println("Merge : "+fileList.size());
           
           n_way_merge(fileList, outputFile, blocksize);
           
           System.out.println("Merge Done");
           System.out.println();
           
        }
        else{
           for (File f : fileArr) { // (nblocks-1)개 될때마다 n_way merge
              fileList.add(f);
              if(fileList.size() == nblocks-1) {
                 System.out.print("pass : " + step + "  run : " + number_of_run);
                  System.out.print(" Merge : "+fileList.size());
                 
                 String runpath = passDirpath + File.separator + "run" + number_of_run + ".data";
                 File runfile = new File(runpath);
                 runfile.createNewFile();
                 number_of_run++;
                 n_way_merge(fileList, runpath, blocksize);
                 fileList.clear();
              }
           }
           if(fileList.size()>0) {// 이제 (nblocks-1)개보다 적은 나머지 n_way_merge
              System.out.print("pass : " + step + "  run : " + number_of_run);
               System.out.print(" Merge : "+fileList.size());

               String runpath = passDirpath + File.separator + "run" + number_of_run + ".data";
               File runfile = new File(runpath);
               runfile.createNewFile();
               number_of_run++;
               n_way_merge(fileList, runpath, blocksize);
               fileList.clear();
           }
            _externalMergeSort(tmpDir, outputFile, nblocks, blocksize, step+1);
        }
    }

    public void n_way_merge(ArrayList<File> files, String outputFile, int blocksize) throws IOException {
       
        System.out.println(" [Merging]");
        
        PriorityQueue<DataManager> queue = new PriorityQueue<>(files.size(), new Comparator<DataManager>() {
                    public int compare(DataManager o1, DataManager o2) { 
                        return o1.tuple.compareTo(o2.tuple);
                    }
                });
        
        for (File f: files) {
           try {
              DataManager dm = new DataManager();
              dm.openDis(f.getAbsolutePath(), blocksize);
              dm.readNext();
              queue.add(dm);
           } catch (EOFException e) {
           }
        }
        
        DataManager out = new DataManager();
        DataManager q_dm = new DataManager();
        out.openDos(outputFile, blocksize);
        while (queue.size() != 0){
           try {
              q_dm = queue.poll();
              MutableTriple<Integer, Integer, Integer> tmp = new MutableTriple<>();
              q_dm.getTuple(tmp);
              out.writeNext(tmp);
              q_dm.readNext();
              queue.add(q_dm);
           } catch (EOFException e) {
              q_dm.closeDis();
           }
        }
        out.closeDos();
    }

    class DataManager{
        public boolean isEOF = false;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        public MutableTriple<Integer, Integer, Integer> tuple = new MutableTriple<Integer, Integer, Integer>(0, 0, 0);
        public DataManager() throws IOException {
        }
        private void openDis(String path, int blocksize) throws FileNotFoundException {
            this.dis = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(path), blocksize)
            );
        }
        private void openDos(String path, int blocksize) throws FileNotFoundException {
            this.dos = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(path), blocksize)
            );
        }
        private void closeDis() throws IOException {
            this.dis.close();
        }
        private void closeDos() throws IOException {
            this.dos.close();
        }
        private void readNext() throws IOException {
            tuple.setLeft(dis.readInt()); tuple.setMiddle(dis.readInt()); tuple.setRight(dis.readInt());
        }
        private void getTuple(MutableTriple<Integer, Integer, Integer> ret) throws IOException {
            ret.setLeft(tuple.getLeft()); ret.setMiddle(tuple.getMiddle()); ret.setRight(tuple.getRight());
        }
        private void writeNext(MutableTriple<Integer, Integer, Integer> ret) throws IOException {
            dos.writeInt(ret.getLeft()); dos.writeInt(ret.getMiddle()); dos.writeInt(ret.getRight());
        }
    }
}
*/


