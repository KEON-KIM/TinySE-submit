package edu.hanyang.submit;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class Node {
	/* status 
	 * 0 : root
	 * 1 : non leaf
	 * 2 : leaf
	 */
	int offset;
	int status; 
	int node_size;
	int max_num;
	int blocksize;
	List<Integer> vals;
	List<Integer> keys;
	
	protected abstract Node copyNode();
	abstract ByteBuffer to_tree_buffer();
	abstract ByteBuffer to_tree_buffer(int index);
	abstract void insert(int key, int val) ;
	abstract int get_value(int key); 
	/* file을 쓰고 읽을때 꼭 필요함.*/
	public void set_node_size() {
		this.node_size = (this.keys.size() - 1 + this.vals.size());
	}
	public boolean isOver() {
		if(keys.size()-1 >= this.max_num) return true;
		return false;
	}
	public ByteBuffer to_meta_buffer() {
		byte[] buffer = new byte[12];
		ByteBuffer bf = ByteBuffer.wrap(buffer);
		bf.putInt(this.status);
		bf.putInt(this.offset);
		bf.putInt(this.node_size);
		bf.clear();
		return bf;
	}
	/* index부터 keys, vals remove*/

	
	
}
