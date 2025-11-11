package project212;
class Node<T>{
	public T data;
	public Node<T> next;
	public Node(T val) {
		data=val;
		next=null;
	}
}
public class LinkedList<T> {
	private Node<T> head;
	private Node<T> current;
	public LinkedList() {
		head =current =null;
	}
	public boolean empty() {
		return head == null;
	}
	public boolean last() {
		return current.next == null;
	}
	public boolean full() {
		return false;
	}
	public void findfirst() {
		current = head;
	}
	public void findenext() {
		current = current.next;
	}
	public T retrieve() {
		return current.data;
	}
	public void update(T val) {
		current.data = val;
	}
	public void insert(T val) {
		Node<T> tmp;
		if(empty()) {
			current = head = new Node<T> (val);
		}
		else {
			tmp = current.next;
			current.next = new Node<T> (val);
			current = current.next;
			current.next = tmp;
		}
	}
	public void remove() {
		if(current == head) {
			head = head.next;
		}
		else {
			Node<T> tmp = head;
			while(tmp.next != current)
				tmp = tmp.next;
			tmp.next = current.next;
		}
		if(current.next == null)
			current = head;
		else
			current = current.next;
	}
	
	public void display() {
		if(head == null)
			System.out.println("empty list");
		Node<T> p = head;
		while(p != null) {
			System.out.println(p.data+"  ");
			p = p.next;
		}
	}
	public boolean exists(T e) {
		Node<T> exes = head;
		while(exes != null) {
			if(exes.data.equals(e))
				return true;
			exes = exes.next;
		}
		return false;
	}
	public void addLast(T a) {
		Node<T> temp = new Node<>(a);
		if(head == null)
			current=head=temp;
		else {
			while(current.next != null)
				current=current;
			current.next=temp;
			current=temp;
		}
	}
	public int size() {
		int n=0;
		Node<T> p = head;
		while(p != null) {
			n++;
			p=p.next;
		}
		return n;
	}
	
}
