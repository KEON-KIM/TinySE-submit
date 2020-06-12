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
	static ByteBuffer bf;
	List<Integer> vals;
	List<Integer> keys;
	
	protected abstract Node copyNode();
	abstract byte[] to_tree_buffer();
	abstract byte[] to_tree_buffer(int index);
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
	public void intTobyte(byte[] buffer, int value, int i) {
		buffer[i] = (byte)(value >> 24);
		buffer[i+1] = (byte)(value >> 16);
		buffer[i+2] = (byte)(value >> 8);
		buffer[i+3] = (byte)(value);
	}
	public byte[] to_meta_buffer() {
		byte[] buffer = new byte[12];
		intTobyte(buffer, this.status, 0);
		intTobyte(buffer, this.offset, 4);
		intTobyte(buffer, this.node_size, 8);
		return buffer;
	}
	/* index부터 keys, vals remove*/

	
	
}
