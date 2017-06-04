package patternFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import patternParser.*;

public class MyRuleFinder {
	
	CompilationUnit rootCu;
	Rule rule;
	ArrayList<BasicNode> rulePatterns;
	Map<BasicNode, MyASTVisitor> correspondencies;
	
	
	public MyRuleFinder(CompilationUnit rootCu, Rule rule) {
		
		this.rootCu = rootCu;
		this.rule = rule;
		
		this.rulePatterns = rule.getChildren();
		
		this.correspondencies = new HashMap<>();
	}
	
	public void search(ASTNode parentNode){
		
		// Search on every node of the java code
		for(ASTNode node : getASTNodeChildren(parentNode)){
			
			System.out.println("Node: " + node);
			System.out.println("Node type: " + node.getNodeType());
			
			// Search for all the pattern nodes
			for(BasicNode pattern : this.rulePatterns){
				
				MyASTVisitor myASTVisitor = new MyASTVisitor(node, pattern);
				
				if(myASTVisitor.isFound()){
					this.correspondencies.put(pattern, myASTVisitor);
				}
				
			}
			
			search(node);
		}	
		
	}
	
	public boolean verifySameParent(){
		
		ASTNode matchingNode = null;
		
		for(MyASTVisitor successfulVisitor: this.correspondencies.values()){
			
			if(matchingNode == null)
				matchingNode = successfulVisitor.getCorrespondingNode().getParent();
			
			if(successfulVisitor.getCorrespondingNode().getParent() != matchingNode)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean verifySameOrder(){
		
	
		int currentPosition = -1;
		
		for(BasicNode pattern : this.rulePatterns){
			
			int correspondencePosition = this.correspondencies.get(pattern).getCorrespondingNode().getStartPosition();
			
			// The position must always be higher than the previous, obeying the order of the list
			if(correspondencePosition > currentPosition)
			{
				currentPosition = correspondencePosition;
			}
			else
			{
				return false;
			}
			
		}
		return true;
	}
	
	public static List<ASTNode> getASTNodeChildren(ASTNode node) {
	    List<ASTNode> children = new ArrayList<ASTNode>();
	    
	    List list = node.structuralPropertiesForType();
	    
	    for (int i = 0; i < list.size(); i++) {
	    	
	        Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
	        
	        if (child instanceof ASTNode) {
	            children.add((ASTNode) child);
	        }
	        else if(child instanceof List){
	        	
	        	for(int j = 0; j < ((List) child).size(); j++){
	        		
	        		children.add((ASTNode)(((List) child).get(j)));
	        		
	        	}
	        	
	        }
	        
	    }
	    return children;
	}

}
