package edu.hanyang.submit;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class nonleafNode extends Node{
	nonleafNode(ByteBuffer tree_buffer, int blocksize, ByteBuffer meta_buffer) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		
		this.status = meta_buffer.getInt();
		this.offset = meta_buffer.getInt();
		int i;
		for(i = 0; i < tree_buffer.capacity()/8; i++) {
			this.vals.add(tree_buffer.getInt());
			this.keys.add(tree_buffer.getInt());
		}	
		this.vals.add(tree_buffer.getInt());
		meta_buffer.clear();
		tree_buffer.clear();
	}
	nonleafNode(ByteBuffer tree_buffer, int blocksize, int status, int offset) {
		this.max_num = (blocksize / Integer.BYTES) / 2;
		this.blocksize = blocksize;
		this.keys = new ArrayList<>(this.max_num);
		this.keys.add(-1);
		this.vals = new ArrayList<>(this.max_num);
		
		this.status = status;
		this.offset = offset;
		int i;
		for(i = 0; i < tree_buffer.capacity()/8; i++) {
			this.vals.add(tree_buffer.getInt());
			this.keys.add(tree_buffer.getInt());
		}	
		this.vals.add(tree_buffer.getInt());
		tree_buffer.clear();
	}
	
	nonleafNode(int blocksize, int status, int offset) {
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
        this.vals.add(low, val);
        this.keys.add(low, key);
	}
	public ByteBuffer to_tree_buffer(){
		this.set_node_size();
		ByteBuffer bf = ByteBuffer.wrap(new byte[this.node_size*4]);
		int i;
		for(i = 0; i < this.node_size / 2; i++) {
			bf.putInt(this.vals.get(i));
			bf.putInt(this.keys.get(i+1));
		}
		bf.putInt(this.vals.get(i));
		bf.clear();
		return bf;
	}
	public ByteBuffer to_tree_buffer(int index){
		this.node_size = 2*index -1;
		int num_buffer = this.keys.size() -1 + this.vals.size() - this.node_size - 1;
		ByteBuffer bf = ByteBuffer.wrap(new byte[num_buffer*4]);
		int i;
		for(i = 0; i < num_buffer / 2 ; i++) {
			bf.putInt(this.vals.get(index+i));
			bf.putInt(this.keys.get(index+1+i));	
		}
		bf.putInt(this.vals.get(index+i));
		this.keys = this.keys.subList(0, index);
		this.vals = this.vals.subList(0, index);
		bf.clear();
		return bf;
		
	}
	public Node copyNode(){
		return new nonleafNode(this.to_tree_buffer(), this.blocksize, this.to_meta_buffer());
	}
	
	public int get_value(int key){ 
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
        return this.vals.get(low - 1);
	}
}
