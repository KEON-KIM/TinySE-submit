package edu.hanyang;
 
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
 
import edu.hanyang.submit.TinySEBPlusTree;

// @Ignore("Delete this line to unit test stage 3")
//@Ignore("Delete this line to unit test stage 3")
public class BPlusTreeTest {

	
	@Test
	public void bPlusTreeTestWithLargeFile() throws IOException {
		String metapath = "./tmp/bplustree.meta";
		String savepath = "./tmp/bplustree.tree";
		int blocksize = 4096;
		int nblocks = 2000;

		File metafile = new File(metapath);
		File treefile = new File(savepath);
		if (treefile.exists()) {
			if (! treefile.delete()) {
				System.err.println("error: cannot remove tree file");
				System.exit(1);
			}
		}
		if (metafile.exists()) {
			if (! metafile.delete()) {
				System.err.println("error: cannot remove meta file");
				System.exit(1);
			}
		}

		TinySEBPlusTree tree = new TinySEBPlusTree();
		tree.open(metapath, savepath, blocksize, nblocks);
		int mb = 1024*1024;
		long startTime = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(this.getClass().getClassLoader().getResource("stage3-500000.data").getFile())))) {
			while (in.available() > 0) {
				int termid = in.readInt();
				int addr = in.readInt();

				tree.insert(termid, addr);
//				System.out.println((runtime.totalMemory() - runtime.freeMemory()) / mb);
			}
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		double duration = (double)(System.currentTimeMillis() - startTime)/1000;
		System.out.println("Used Memory : " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		System.out.println("Inserting time duration: " + duration);

		tree.close();

		tree = new TinySEBPlusTree();
		tree.open(metapath, savepath, blocksize, nblocks);

		startTime = System.currentTimeMillis();

		runtime = Runtime.getRuntime();
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(this.getClass().getClassLoader().getResource("stage3-500000.data").getFile())))) {
			while (in.available() > 0) {
				int termid = in.readInt();
				int addr = in.readInt();
				assertEquals(tree.search(termid), addr);
				//System.out.println((runtime.totalMemory() - runtime.freeMemory()) / mb);
			}
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(1);
		}
		duration = (double)(System.currentTimeMillis() - startTime)/1000;
		System.out.println("Used Memory : " + (runtime.totalMemory() - runtime.freeMemory())/mb);
		System.out.println("Searching time duration: " + duration);
		tree.close();
	}
}