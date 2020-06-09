package edu.hanyang.submit;

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
	byte[] buffer;
	List<Integer> vals;
	List<Integer> keys;
	
	protected abstract Node copyNode();
	abstract byte[] to_tree_buffer();
	abstract byte[] to_tree_buffer(int index);
	abstract void insert(int key, int val) ;
	/* file을 쓰고 읽을때 꼭 필요함.*/
	public void set_node_size() {
		this.node_size = (this.keys.size() - 1 + this.vals.size());
	}
	public boolean isOver() {
		if(keys.size()-1 >= this.max_num) return true;
		return false;
	}
	public void intTobyte(int value, int i) {
		this.buffer[i] = (byte)(value >> 24);
		this.buffer[i+1] = (byte)(value >> 16);
		this.buffer[i+2] = (byte)(value >> 8);
		this.buffer[i+3] = (byte)(value);
	}
	public byte[] to_meta_buffer() {
		this.buffer = new byte[12];
		intTobyte(this.status, 0);
		intTobyte(this.offset, 4);
		intTobyte(this.node_size, 8);
		return this.buffer;
	}
	/* index부터 keys, vals remove*/

	
	abstract int get_value(int key); 
//	{
//        int low = 0;
//        int high = this.keys.size() - 1;
//        while (low <= high) {
//            int mid = low + (high - low)/2; // mid 값을 계산.
//            if (key > this.keys.get(mid)) // 키값이 더 크면 왼쪽을 버린다.
//                low = mid + 1;
//            else if (key < this.keys.get(mid)) // 키값이 더 작으면 오른쪽을 버린다.
//                high = mid - 1;
//            else
//                return this.vals.get(mid - 1); // key found
//        }
//        return -1;  // key not found
//		
//	}
}
