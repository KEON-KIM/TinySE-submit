package edu.hanyang.submit;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class leafNode extends Node {
	
	leafNode(ByteBuffer tree_buffer, int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
		
		for(int i = 0; i < tree_buffer.capacity()/8; i++) {
			this.vals.add(tree_buffer.getInt());
			this.keys.add(tree_buffer.getInt());
		}
		tree_buffer.clear();
	}
	
	leafNode(ByteBuffer tree_buffer, int blocksize, ByteBuffer meta_buffer) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = meta_buffer.getInt();
		this.offset = meta_buffer.getInt();
		for(int i = 0; i < tree_buffer.capacity()/8; i++) {
			this.vals.add(tree_buffer.getInt());
			this.keys.add(tree_buffer.getInt());
		}	
		meta_buffer.clear();
		tree_buffer.clear();
		
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
	
	public ByteBuffer to_tree_buffer() {
		this.set_node_size();
		ByteBuffer bf = ByteBuffer.wrap(new byte[this.node_size*4]);
		int i;
		for(i = 0; i < this.node_size / 2; i++) {
			bf.putInt(this.vals.get(i));
			bf.putInt(this.keys.get(i+1));
		}
		bf.clear();
		return bf;
	}
	public ByteBuffer to_tree_buffer(int index) {
		this.node_size = 2*(index - 1);
		int num_buffer = this.keys.size() + this.vals.size() - this.node_size - 1;
		ByteBuffer bf = ByteBuffer.wrap(new byte[num_buffer*4]);
		for(int i = 0; i < num_buffer/2 ; i++) {
			bf.putInt(this.vals.get(index-1+i));
			bf.putInt(this.keys.get(index+i));
		}
		this.vals = this.vals.subList(0, index-1);
		this.keys = this.keys.subList(0, index);
		bf.clear();
		return bf;
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
