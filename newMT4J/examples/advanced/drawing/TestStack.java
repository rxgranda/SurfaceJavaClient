package advanced.drawing;

import java.util.Stack;

public class TestStack {

	public static void main(String []args){
		Stack<String> stack = new Stack<String>(){
		    private static final long serialVersionUID = 1L;
		    public String push(String item) {
		        if (this.size() == 3) {
		            this.removeElementAt(0);
		        }
		        return super.push(item);
		    }
		};
		int i=0;
		stack.push(""+i);
		i++;
		stack.push(""+i);
		i++;

		stack.push(""+i);
		i++;
		imprimir(stack);
		stack.push(""+i);
		i++;
		imprimir(stack);
		stack.push(""+i);
		i++;
		imprimir(stack);

	}
	static void imprimir(Stack<String> stack){
		for(String a:stack){
			System.out.println(a);
			
		}
		System.out.println("--------");
	}
}
