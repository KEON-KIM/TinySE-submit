package edu.hanyang.submit;

import java.io.IOException;

import edu.hanyang.indexer.DocumentCursor;
import edu.hanyang.indexer.PositionCursor;
import edu.hanyang.indexer.IntermediateList;
import edu.hanyang.indexer.IntermediatePositionalList;
import edu.hanyang.indexer.QueryPlanTree;
import edu.hanyang.indexer.QueryProcess;
import edu.hanyang.indexer.StatAPI;

public class TinySEQueryProcess implements QueryProcess {

	@Override
	public void op_and_w_pos(DocumentCursor op1, DocumentCursor op2, int shift, IntermediatePositionalList out)
			throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void op_and_wo_pos(DocumentCursor op1, DocumentCursor op2, IntermediateList out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryPlanTree parse_query(String query, StatAPI stat) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}

















/*
File dir = new File(tmpdir);
if(!dir.exists()){
	dir.mkdirs();
}

DataInputStream input = new DataInputStream(
			new BufferedInputStream(
				new FileInputStream(infile),blocksize));
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
*/