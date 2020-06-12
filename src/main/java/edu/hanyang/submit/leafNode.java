package edu.hanyang.submit;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class leafNode extends Node {
	
	leafNode(byte[] tree_buffer, int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
		bf = ByteBuffer.wrap(tree_buffer);
		for(int i = 0; i < tree_buffer.length/8; i++) {
			this.vals.add(bf.getInt());
			this.keys.add(bf.getInt());
		}
		bf.clear();
	}
	
	leafNode(byte[] tree_buffer, int blocksize, byte[] meta_buffer) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		bf = ByteBuffer.wrap(meta_buffer);
		this.status = bf.getInt();
		this.offset = bf.getInt();
		bf.clear();
		bf = ByteBuffer.wrap(tree_buffer);
		for(int i = 0; i < tree_buffer.length/8; i++) {
			this.vals.add(bf.getInt());
			this.keys.add(bf.getInt());
		}	
		bf.clear();
		
	}
	leafNode(int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
	}
	
	public void insert(int key, int val) {
        int low = 0;
        int high = this.keys.size();
        while (low < high) {
            final int mid = low + (high - low)/2;
            if (key >= this.keys.get(mid)) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        this.vals.add(low-1, val);
        this.keys.add(low, key);
	}
	
	public byte[] to_tree_buffer() {
		this.set_node_size();
		byte[] buffer = new byte[this.node_size*4];
		int i;
		for(i = 0; i < this.node_size / 2; i++) {
			this.intTobyte(buffer, this.vals.get(i), i*8);
			this.intTobyte(buffer, this.keys.get(i+1), 4+i*8);
		}
		return buffer;
	}
	public byte[] to_tree_buffer(int index) {
		this.node_size = 2*(index - 1);
		int num_buffer = this.keys.size() + this.vals.size() - this.node_size - 1;
		byte[] buffer = new byte[num_buffer*4];
		for(int i = 0; i < num_buffer/2 ; i++) {
			this.intTobyte(buffer, this.vals.get(index-1+i), i*8);
			this.intTobyte(buffer, this.keys.get(index+i), 4+i*8);
		}
		this.vals = this.vals.subList(0, index-1);
		this.keys = this.keys.subList(0, index);
		return buffer;
	}
	public Node copyNode(){
		return new leafNode(this.to_tree_buffer(), this.blocksize, this.to_meta_buffer());
	}
	public int get_value(int key) {
        int low = 0;
        int high = this.keys.size() - 1;
        while (low <= high) {
            int mid = low + (high - low)/2; // mid 값을 계산.
            if (key > this.keys.get(mid)) // 키값이 더 크면 왼쪽을 버린다.
                low = mid + 1;
            else if (key < this.keys.get(mid)) // 키값이 더 작으면 오른쪽을 버린다.
                high = mid - 1;
            else
                return this.vals.get(mid - 1); // key found
        }
        return -1;  // key not found
		
	}
}
