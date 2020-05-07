/*
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

		File dir = new File(tmpdir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(infile),blocksize));
		DataOutputStream run_writer;
		ArrayList<MutableTriple<Integer, Integer, Integer>> runs = new ArrayList<MutableTriple<Integer, Integer, Integer>>();
		int word_id, doc_id, pos;
		int run_cnt = 1;
		int pass_cnt = 1;
		int run_control = 80;
		
		
		while(input.available() != 0){
			if( input.available() > nblocks * run_control * (Integer.SIZE/Byte.SIZE) * 3  ) {
				while (runs.size() < nblocks * run_control){
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
		// create run 완료
	}
}
*/