package networkmetrics;


import java.util.ArrayList;
import java.util.Iterator;

public interface Graph<Key extends Comparable<Key>,Data> {
	
	
	/**
	* Returns the number of vertices |V|. 
	*	    
	* @return  the number of vertices |V| in the Graph.  
	*/
    public int vertexCount();
   
    /**
	* Returns the number of arcs |A| in the Graph. 
	*	    
	* @return   Returns the number of arcs |A|in the Graph.   
	*/
    public int arcCount();
    
    /**
	*  Returns an iterator over the arcs (directed edges) A of G. 
	*	    
	* @return   Returns an iterator over the arcs (directed edges) A of G.
	* Arcs are represented by an ArrayList that contains the Vertex Key of the source 
	*  destination at position 0 and   the Vertex Key of the destination at position 1 
	*/
    public Iterator<ArrayList<Key>> arcs();
    
    /**
	*  Returns an iterator over the vertices V
	* 
	*	    
	* @return   Returns an iterator over the VertexKeys V.
	*/
    public Iterator<Key> vertices();
    
       
    /**
	* Returns TRUE if there exists an arc connecting a source vertex with the Key sourceKey  a target vertex targetKey, otherwise FALSE.
	*	    
	* @return  Returns the Arc that connects client keys source and target, or null if none.
	*/
    public boolean arcExists(Key sourceKey, Key destinationKey);
    
    /**
   	* Returns TRUE if there vertex with the Key, otherwise false.
   	*	    
   	* @return  Returns TRUE if there vertex with the Key, otherwise false.
   	*/
    public boolean vertexExists(Key vertexKey);
       
  
    /**
   	*   Returns the number of arcs incoming to v.
   	*	    
   	* @return   Returns the number of arcs incoming to v.
   	*/     
    public int inDegree(Key vertexKey);
        

    /**
   	*   Returns the number of arcs outgoing from v.
   	*	    
   	* @return   Returns the number of arcs outgoing from v.
   	*/  
    public int outDegree(Key vertexKey);
        

    /**
    *    Returns an iterator over the vertices adjacent to v by incoming arcs.
    *	    
    * @return   Returns an iterator over the vertices adjacent to v by incoming arcs.
    */  
    public Iterator<ArrayList<Key>> inAdjacentVertices(Key vertexKey);
       

    /**
    * Returns an iterator over the vertices adjacent to v by outgoing arcs.
    *	    
    * @return  Returns an iterator over the vertices adjacent to v by outgoing arcs.
    */  
    public Iterator<ArrayList<Key>> outAdjacentVertices(Key vertexKey);
        
    /**
    *   Returns the client data Object associated with vertex keyed by key.
    *	    
    * @return    Returns the client data Object associated with vertex keyed by key.
    */  
    public Data getVertexData(Key vertexKey);
       

    /**
    *   Returns the client data Object associated with arc (sourceKey, destinationKey).
    *	    
    * @return    Returns the client data Object associated with arc (sourceKey, destinationKey).
    */  
    public Data getArcData(Key sourceKey, Key destinationKey);
       
    
    /**
    *  Returns the weight on arc (key1, key2). If none has been assigned, returns Integer 1.
    *	    
    * @return   Returns the weight on arc (key1, key2). If none has been assigned, returns Integer 1.
    */  
    public Number getArcWeight(Key sourceKey, Key destinationKey);
       
  
        
   // These are the methods by which you build and change graphs.

    /**
     *  Inserts a new isolated vertex indexed under (retrievable via) key and optionally containing an object data (which defaults to null).
     *	    
     */  
    public void insertVertex(Key vertexKey);
    
    /**
    * Inserts a new isolated vertex indexed under (retrievable via) key and optionally containing an object data (which defaults to null).
    *	    
    */  
    public void insertVertex(Key vertexKey, Data vertexData);
     
    /**
     * Inserts a new arc from an existing vertex to another, optionally containing an object data.
     *	    
     */  
     public  void insertArc(Key sourceKey, Key destinationKey);
  
     /**
     * Inserts a new arc from an existing vertex to another, optionally containing an object data.
     *	    
     */  
    public void insertArc(Key sourceKey, Key destinationKey, Data arcData);
      
    /**
     * Changes the data Object associated with Vertex v to data.
     *	    
     */  
    public void setVertexData(Key vertexKey, Data vertexData);
        

    /**
     * Changes the data Object associated with Arc a to arcData.
     *	    
     */   
    public void setArcData(Key sourceKey, Key destinationKey, Data arcData);
      
    /**
     * Deletes a vertex and all its incident arcs (and edges under the undirected extension).
     *	    
     * @return Returns the client data object formerly stored at v.
     */  
    public Data removeVertex(Key vertexKey);
        

    /**
    * Removes an arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    * @return Returns the client data object formerly stored at arc with Source vertex sourceKey , Key destinationKey.
    */   
    public Data removeArc(Key sourceKey, Key destinationKey);
        
    /**
    * Reverse the direction of an arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */  
    public void reverseDirection(Key sourceKey, Key destinationKey) ;
        
    /**
    * Reverse the direction of all arcs of the graph in place (modifies the graph). 
    *	    
    */  
    public void transposeGraph() ;
    
    /**
    * Sets the weight on Arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */  
    public void setArcWeight(Key sourceKey, Key destinationKey, Number weight);
    
    /**
    * Adds or changes the Value value for the Property property to a Vertex vertex having a Key vertexKey.
    *	    
    */  
    public void setAnnotation(Key vertexKey, Object property, Object value); 
   
    /**
    *  Adds or changes the Value for the Property property of an Arc with Source vertex having a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */ 
    public void setAnnotation(Key sourceKey, Key destinationKey, Object property , Object value); 
    
    /**
     * Returns the value for Property property of the vertex with the Key vertexKey.
     *
     * @return Returns the value for Property property for the Vertex.
     * 
     */ 
     public Object getAnnotation(Key vertexKey, Object property);
    
    /**
    * Returns the value for for the Property property of an Arc with Source vertex having a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *
    * @return Returns the value for for the Property property for the Arc.  
    */ 
    public Object getAnnotation(Key sourceKey, Key destinationKey, Object property ); 
   
     /**
     * Returns the value for the property that was removed indexed by the Key annotationKey a vertex with the Key vertexKey.
     * @return Returns the value for the property that was removed from the vertex.
     */ 
    public Object removeAnnotation(Key vertexKey,Object property) ;

    /**
    * Remove the value for the Property property of the arc with the with Source vertex sourceKey , Key destinationKey.
    *
    * @return  Returns the value for the Property property that was removed from the arc.   
    */ 
    public Object removeAnnotation( Key sourceKey, Key destinationKey, Object property ) ;
   

    /**
    *  Removes all values on vertices or arcs for the Property property. Use this to clean up between runs. 
    *	    
    */ 
    public void clearAnnotations(Object property) ;
    
}
