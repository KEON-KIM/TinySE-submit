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

//@Ignore("Delete this line to unit test stage 2")
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
		File infile = new File(classLoader.getResource("test.data").getFile());
		String outfile = "./tmp/sorted.data";
		String tmpdir = "./tmp";
		File resultFile = new File(outfile);
		
		TinySEExternalSort sort = new TinySEExternalSort();
		long timestamp = System.currentTimeMillis();
		sort.sort(infile.getAbsolutePath(), outfile, tmpdir, blocksize, nblocks);
		System.out.println("time duration: " + (System.currentTimeMillis() - timestamp) + " msecs with " + nblocks + " blocks of size " + blocksize + " bytes");

		
		File answerFile = new File(classLoader.getResource("answer.data").getFile());
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
	for(Triple<Integer,Integer,Integer> tuple : runs){
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
*/







