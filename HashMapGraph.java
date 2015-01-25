package networkmetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This will include a two layered HashMap. Then outer layer will include vertices, and the inner the in and out edges of the vertices.
 * This will include also the three dimensional HashMap, with the first layer for properties that the user may store. Then the next two layers
 * are just like the graph vertices and edges. 
 * @author Joshua Weldon
 *
 * @param <Key> The objects for the vertices and edges
 * @param <Data> the data that will be stored for each vertex and edge
 */
public class HashMapGraph<Key extends Comparable<Key>, Data> implements Graph<Key, Data>  {
	
	public static final int EDGE_TABLE_INITIAL_CAPACITY      = 4;
	public static final int EDGE_LIST_INITIAL_CAPACITY       = 2;
	public static final boolean OUT = true;
	
	/**
	 * An edge will include data and weight
	 * @author Joshua Weldon
	 *
	 * @param <D> the data type for the data to be stored 
	 */
	private class Edge<D>{
		private D      data;
		private Number weight;
		
		public Edge(){
			data   = null;
			weight = 0;
		}
		
		public Edge(D data){
			this.data = data;
			weight    = 0;
			
		}
	}
	
	/**
	 * The vertex will have data and two HashMaps for the in and out edges associated with it.
	 * @author Joshua Weldon
	 *
	 * @param <D> the data type to be stored
	 */
	private class Vertex<D>{
		private HashMap<Key, Edge<D>> outEdges;
		private HashMap<Key, Edge<D>> inEdges;
		private D                     data;
		
		public Vertex(){
			outEdges = new HashMap<Key, Edge<D>>(EDGE_TABLE_INITIAL_CAPACITY);
			inEdges  = new HashMap<Key, Edge<D>>(EDGE_TABLE_INITIAL_CAPACITY);
			data     = null;
		}
		
		public Vertex(D data){
			this();
			this.data = data;
		}
	}
	
	/**
	 * This graph iterator can iterated through every edge in the graph.
	 * 
	 * @author Joshua Weldon
	 *
	 */
	private class GraphIterator implements Iterator<ArrayList<Key>>{
		
		private Iterator<Key> vertexIterator;
		private Iterator<Key> edgeIterator;
		private Key           source;
		private Key           destination;
		
		public GraphIterator(){
			
			vertexIterator = vertices.keySet().iterator();
			
			do{
				if(vertexIterator.hasNext())
					source = vertexIterator.next();
			
				else break;
				
				if(!transpose) edgeIterator = vertices.get(source).outEdges.keySet().iterator();
				else           edgeIterator = vertices.get(source).inEdges.keySet().iterator();
			
			}while(!edgeIterator.hasNext());
			
			if(edgeIterator != null && edgeIterator.hasNext()){
				destination = edgeIterator.next();
			}
			
		}
		

		public boolean hasNext() {
			
			if(source == null || destination == null) return false;
			
			else return true;
			
		}


		public ArrayList<Key> next() {
			
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			
			ArrayList<Key> list = new ArrayList<Key>();
			list.add(source);
			list.add(destination);
			
		
			while(!edgeIterator.hasNext()){
			
				 if(vertexIterator.hasNext())
					source = vertexIterator.next();
			
				else{
					source = null;
					break;
				}
				
				if(!transpose)	edgeIterator = vertices.get(source).outEdges.keySet().iterator();
				else            edgeIterator = vertices.get(source).inEdges.keySet().iterator();
			}
			
			if(edgeIterator.hasNext())
				destination = edgeIterator.next();
		
			return list;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * This iterator will only iterate through adjacent edges of a given vertex.
	 * 
	 * @author Joshua Weldon
	 *
	 */
	private class AdjacentIterator implements Iterator<ArrayList<Key>>{
		private Iterator<Key> edgeIterator;
		private Key           source;
		private Key           destination;
		private boolean       outList;  
		
		public AdjacentIterator(Key key, boolean outList){
			this.outList = outList;
			
			if(outList){
				source = key;
				edgeIterator = vertices.get(source).outEdges.keySet().iterator();
			
				if(edgeIterator.hasNext())
					destination = edgeIterator.next();	
			}
			
			//inList
			else{
				destination  = key;
				edgeIterator = vertices.get(destination).inEdges.keySet().iterator();
			
				if(edgeIterator.hasNext())
					source = edgeIterator.next();
			}		
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			if(destination == null || source == null) return false;
			else return true;
		}

		public ArrayList<Key> next() {
			if(!hasNext())
				throw new NoSuchElementException();
			
			ArrayList<Key> list = new ArrayList<Key>();
			list.add(source);
			list.add(destination);
			
			if(outList){
				 
				if(edgeIterator.hasNext())
					destination = edgeIterator.next();
				else destination = null;
			}
			
			//inList
			else{
			
				if(edgeIterator.hasNext())
					source = edgeIterator.next();
				else source = null;
			}	
			return list;
		}
	}
	
	private HashMap<Key, Vertex<Data>> vertices;
	private HashMap<Object, HashMap<Key,Vertex<Object>>> annotationVertices;
	private int     edgeCount;
	private int     vertexCount;
	private boolean transpose;

	/**
	 * Constructs the graph
	 */
	public HashMapGraph(){
		vertices           = new HashMap<Key,Vertex<Data>>();
		annotationVertices = new HashMap<Object, HashMap<Key,Vertex<Object>>>();
		edgeCount   = 0;
		vertexCount = 0;
		transpose   = false;
	}

	/**
	* Returns the number of vertices |V|. 
	*	    
	* @return  the number of vertices |V| in the Graph.  
	*/
    public int vertexCount(){
    	return vertexCount;
    }
   
    /**
	* Returns the number of arcs |A| in the Graph. 
	*	    
	* @return   Returns the number of arcs |A|in the Graph.   
	*/
    public int arcCount(){
    	return edgeCount;
    }
    
    /**
	*  Returns an iterator over the arcs (directed edges) A of G. 
	*	    
	* @return   Returns an iterator over the arcs (directed edges) A of G.
	* Arcs are represented by an ArrayList that contains the Vertex Key of the source 
	*  destination at position 0 and   the Vertex Key of the destination at position 1 
	*/
    public Iterator<ArrayList<Key>> arcs(){
    	return new GraphIterator();
    }
    
    /**
	*  Returns an iterator over the vertices V
	* 
	*	    
	* @return   Returns an iterator over the VertexKeys V.
	*/
    public Iterator<Key> vertices(){
    	return vertices.keySet().iterator();
    }
    
       
    /**
	* Returns TRUE if there exists an arc connecting a source vertex with the Key sourceKey  a target vertex targetKey, otherwise FALSE.
	*	    
	* @return  Returns the Arc that connects client keys source and target, or null if none.
	*/
    public boolean arcExists(Key sourceKey, Key destinationKey){
    	if(sourceKey == null  || destinationKey == null) throw new NullPointerException();
    	
    	if(!vertexExists(sourceKey) || !vertexExists(destinationKey)) return false;
    	
    	if(!transpose){
    		if(vertices.get(sourceKey).outEdges.containsKey(destinationKey)) return true;
    		else return false;
    	}
    	
    	else{
    		if(vertices.get(sourceKey).inEdges.containsKey(destinationKey)) return true;
    		else return false;
    	}
    }
    
    /**
   	* Returns TRUE if there vertex with the Key, otherwise false.
   	*	    
   	* @return  Returns TRUE if there vertex with the Key, otherwise false.
   	*/
    public boolean vertexExists(Key vertexKey){
    	if (vertexKey == null) throw new NullPointerException();
    	
    	if(vertices.containsKey(vertexKey)) return true;
    	return false;
    }
       
  
    /**
   	*   Returns the number of arcs incoming to v.
   	*	    
   	* @return   Returns the number of arcs incoming to v.
   	*/     
    public int inDegree(Key vertexKey){
    	if (vertexKey == null) throw new NullPointerException();
    	
    	if(!vertexExists(vertexKey))throw new NoSuchElementException();
    	
    	if(!transpose)
    		return vertices.get(vertexKey).inEdges.size();
    	else return vertices.get(vertexKey).outEdges.size();
    }
        

    /**
   	*   Returns the number of arcs outgoing from v.
   	*	    
   	* @return   Returns the number of arcs outgoing from v.
   	*/  
    public int outDegree(Key vertexKey){
    	if(vertexKey == null) throw new NullPointerException();
    	
    	if(!vertexExists(vertexKey)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return vertices.get(vertexKey).outEdges.size();
    	else return vertices.get(vertexKey).inEdges.size();
    }
        

    /**
    *    Returns an iterator over the vertices adjacent to v by incoming arcs.
    *	    
    * @return   Returns an iterator over the vertices adjacent to v by incoming arcs.
    */  
    public Iterator<ArrayList<Key>> inAdjacentVertices(Key vertexKey){
    	if (vertexKey == null) throw new NullPointerException();
    	
    	if (!vertexExists(vertexKey)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return new AdjacentIterator(vertexKey, !OUT);
    	else return new AdjacentIterator(vertexKey, OUT);
    }
       

    /**
    * Returns an iterator over the vertices adjacent to v by outgoing arcs.
    *	    
    * @return  Returns an iterator over the vertices adjacent to v by outgoing arcs.
    */  
    public Iterator<ArrayList<Key>> outAdjacentVertices(Key vertexKey){
    	
    	if (vertexKey == null) throw new NullPointerException();
    	
    	if (!vertexExists(vertexKey)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return new AdjacentIterator(vertexKey, OUT);
    	else return new AdjacentIterator(vertexKey, !OUT);
    }
        
    /**
    *   Returns the client data Object associated with vertex keyed by key.
    *	    
    * @return    Returns the client data Object associated with vertex keyed by key.
    */ 
    
    public Data getVertexData(Key vertexKey){
    	if(vertexKey == null) throw new NullPointerException();
    	
    	if(vertexExists(vertexKey)){
    		return vertices.get(vertexKey).data;
    	}
    	
    	else throw new NoSuchElementException();
    }
       

    /**
    *   Returns the client data Object associated with arc (sourceKey, destinationKey).
    *	    
    * @return    Returns the client data Object associated with arc (sourceKey, destinationKey).
    */  
    public Data getArcData(Key sourceKey, Key destinationKey){
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();
    	
    	if(!arcExists(sourceKey, destinationKey)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return vertices.get(sourceKey).outEdges.get(destinationKey).data;
    	
    	else
			return vertices.get(sourceKey).inEdges.get(destinationKey).data;
    		
    }
       
    
    /**
    *  Returns the weight on arc (key1, key2). If none has been assigned, returns Integer 1.
    *	    
    * @return   Returns the weight on arc (key1, key2). If none has been assigned, returns Integer 1.
    */  
    public Number getArcWeight(Key sourceKey, Key destinationKey){
    	
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();
    	
    	if(!arcExists(sourceKey, destinationKey)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return vertices.get(sourceKey).outEdges.get(destinationKey).weight;
    	
    	else
			return vertices.get(sourceKey).inEdges.get(destinationKey).weight;
    }
       
  
        
   // These are the methods by which you build and change graphs.

    /**
     *  Inserts a new isolated vertex indexed under (retrievable via) key and optionally containing an object data (which defaults to null).
     *	    
     */  
    public void insertVertex(Key vertexKey){
    	if(vertexKey == null) throw new NullPointerException();
    	
    	if(vertexExists(vertexKey)) throw new IllegalArgumentException();
    	
    	vertexCount++;
    	vertices.put(vertexKey, new Vertex<Data>());
    }
    
    /**
    * Inserts a new isolated vertex indexed under (retrievable via) key and optionally containing an object data (which defaults to null).
    *	    
    */  
    public void insertVertex(Key vertexKey, Data vertexData){
    	if(vertexKey == null) throw new NullPointerException();
    	
    	if(vertexExists(vertexKey)) throw new IllegalArgumentException();
    	
    	vertexCount++;
    	vertices.put(vertexKey, new Vertex<Data>(vertexData));
    }
     
    /**
     * Inserts a new arc from an existing vertex to another, optionally containing an object data.
     *	    
     */  
     public  void insertArc(Key sourceKey, Key destinationKey){
    	
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();
     	
     	if(!vertexExists(sourceKey) || !vertexExists(destinationKey)) throw new IllegalArgumentException();
     			
     	if(arcExists(sourceKey,destinationKey)) throw new IllegalArgumentException();
     		
     	edgeCount++;
     	Edge<Data> edge = new Edge<Data>();
     	if(!transpose){
     		vertices.get(sourceKey).outEdges.put(destinationKey, edge);
     		vertices.get(destinationKey).inEdges.put(sourceKey,edge);
     	}
     	else{
     		vertices.get(sourceKey).inEdges.put(destinationKey, edge);
     		vertices.get(destinationKey).outEdges.put(sourceKey,edge);
     	}	
     }
  
     /**
     * Inserts a new arc from an existing vertex to another, optionally containing an object data.
     *	    
     */  
    public void insertArc(Key sourceKey, Key destinationKey, Data arcData){
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();
     	
     	if(!vertexExists(sourceKey) || !vertexExists(destinationKey)) throw new IllegalArgumentException();;
     			
     	if(arcExists(sourceKey,destinationKey))throw new IllegalArgumentException();
     		
     	edgeCount++;
     	Edge<Data> edge = new Edge<Data>(arcData);
     	if(!transpose){
     		vertices.get(sourceKey).outEdges.put(destinationKey, edge);
     		vertices.get(destinationKey).inEdges.put(sourceKey,edge);
     	}
     	else{
     		vertices.get(sourceKey).inEdges.put(destinationKey, edge);
     		vertices.get(destinationKey).outEdges.put(sourceKey,edge);
     	}
    }
      
    /**
     * Changes the data Object associated with Vertex v to data.
     *	    
     */  
    public void setVertexData(Key vertexKey, Data vertexData){
    	if(vertexKey == null) throw new NullPointerException();
    	
     	if(!vertexExists(vertexKey)) throw new NoSuchElementException(); 
   
     	vertices.get(vertexKey).data = vertexData;
     	
    }
        

    /**
     * Changes the data Object associated with Arc a to arcData.
     *	    
     */   
    public void setArcData(Key sourceKey, Key destinationKey, Data arcData){
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();

     	if(!arcExists(sourceKey,destinationKey)) throw new IllegalArgumentException();
     			
     	if(!transpose){
     	
 			vertices.get(sourceKey).outEdges.get(destinationKey).data = arcData;
 			vertices.get(destinationKey).inEdges.get(sourceKey).data = arcData;
 			return;
     		
     	}
     	
     	else{
     
 			vertices.get(sourceKey).inEdges.get(destinationKey).data = arcData;
 			vertices.get(destinationKey).outEdges.get(sourceKey).data = arcData;
 			return;
     	}
    }
      
    /**
     * Deletes a vertex and all its incident arcs (and edges under the undirected extension).
     *	    
     * @return Returns the client data object formerly stored at v.
     */  
    public Data removeVertex(Key vertexKey){
    	if(vertexKey == null) throw new NullPointerException();
    	
    	if(!vertexExists(vertexKey)) throw new NoSuchElementException();
    	
    	Data save = vertices.get(vertexKey).data;
    	
    	vertices.remove(vertexKey);
    	
    	for(Vertex<Data> v: vertices.values()){
    		if(v.inEdges.containsKey(vertexKey))
    			v.inEdges.remove(vertexKey);
    		if(v.outEdges.containsKey(vertexKey))
    			v.outEdges.remove(vertexKey);
    	}
    	vertexCount--;
    	return save;
    }
        

    /**
    * Removes an arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    * @return Returns the client data object formerly stored at arc with Source vertex sourceKey , Key destinationKey.
    */   
    public Data removeArc(Key sourceKey, Key destinationKey){
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();

     	if(!arcExists(sourceKey,destinationKey)) throw new NoSuchElementException();
     			
     	Data save;
     	
     	if(!transpose){
     		save = vertices.get(sourceKey).outEdges.get(destinationKey).data;
 			vertices.get(sourceKey).outEdges.remove(destinationKey);
 			vertices.get(destinationKey).inEdges.remove(sourceKey);
     	}
     	
     	else{
     		save = vertices.get(sourceKey).inEdges.get(destinationKey).data;
 			vertices.get(sourceKey).inEdges.remove(destinationKey);
 			vertices.get(destinationKey).outEdges.remove(sourceKey);
     	}
     	
    	edgeCount--;
    	return save;
    }
        
    /**
    * Reverse the direction of an arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */  
    public void reverseDirection(Key sourceKey, Key destinationKey) {
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();

     	if(!arcExists(sourceKey,destinationKey)) throw new IllegalArgumentException();
     	
     	if(arcExists(destinationKey,sourceKey)) throw new IllegalArgumentException();
     	
     	removeArc(sourceKey, destinationKey);
     	
     	insertArc(destinationKey, sourceKey);
    }
        
    /**
    * Reverse the direction of all arcs of the graph in place (modifies the graph). 
    *	    
    */  
    public void transposeGraph() {
    	transpose = !transpose;
    }
    
    /**
    * Sets the weight on Arc with Source vertex having of a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */  
    public void setArcWeight(Key sourceKey, Key destinationKey, Number weight){
    	if(sourceKey == null || destinationKey == null) throw new NullPointerException();

     	if(!arcExists(sourceKey,destinationKey)) throw new IllegalArgumentException();
     			
     	if(!transpose){
     	
 			vertices.get(sourceKey).outEdges.get(destinationKey).weight = weight;
 			vertices.get(destinationKey).inEdges.get(sourceKey).weight = weight;
 			return;
     		
     	}
     	
     	else{
     
 			vertices.get(sourceKey).inEdges.get(destinationKey).weight = weight;
 			vertices.get(destinationKey).outEdges.get(sourceKey).weight = weight;
 			return;
     	}
    }
    
    /**
    * Adds or changes the Value value for the Property property to a Vertex vertex having a Key vertexKey.
    *	    
    */  
    public void setAnnotation(Key vertexKey, Object property, Object value){
    	if(vertexKey == null || property == null || value == null) throw new NullPointerException();
    	
    	if (!vertices.containsKey(vertexKey)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.containsKey(property))
    		annotationVertices.put(property, new HashMap<Key,Vertex<Object>>());
    	
    	if(!annotationVertices.get(property).containsKey(vertexKey))
    		annotationVertices.get(property).put(vertexKey, new Vertex<Object>());
    	
    	annotationVertices.get(property).get(vertexKey).data = value;
    	
    }
   
    /**
    *  Adds or changes the Value for the Property property of an Arc with Source vertex having a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *	    
    */ 
    public void setAnnotation(Key sourceKey, Key destinationKey, Object property , Object value){
    	if(sourceKey == null || destinationKey == null || property == null || value == null) throw new NullPointerException();
    	
    	if (!arcExists(sourceKey,destinationKey)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.containsKey(property))
    		annotationVertices.put(property, new HashMap<Key,Vertex<Object>>());
    	
    	if(!annotationVertices.get(property).containsKey(sourceKey))
    		annotationVertices.get(property).put(sourceKey, new Vertex<Object>());
    	
    	if(!annotationVertices.get(property).containsKey(destinationKey))
    		annotationVertices.get(property).put(destinationKey, new Vertex<Object>());
    	
    	if(!transpose){
    		annotationVertices.get(property).get(sourceKey).outEdges.put(destinationKey, new Edge<Object> (value));
    		annotationVertices.get(property).get(destinationKey).inEdges.put(sourceKey, new Edge<Object> (value));
    	}
    	else{
    		annotationVertices.get(property).get(sourceKey).inEdges.put(destinationKey, new Edge<Object> (value));
    		annotationVertices.get(property).get(destinationKey).outEdges.put(sourceKey, new Edge<Object> (value));
    	}
    }
    
    /**
     * Returns the value for Property property of the vertex with the Key vertexKey.
     *
     * @return Returns the value for Property property for the Vertex.
     * 
     */ 
     public Object getAnnotation(Key vertexKey, Object property){
    	if(vertexKey == null || property == null) throw new NullPointerException();
    	
    	if (!annotationVertices.containsKey(property)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.get(property).containsKey(vertexKey)) throw new NoSuchElementException();
    	
    	return annotationVertices.get(property).get(vertexKey).data;
     }
    
    /**
    * Returns the value for for the Property property of an Arc with Source vertex having a Key of sourceKey and Destination vertex having of a Key of destinationKey.
    *
    * @return Returns the value for for the Property property for the Arc.  
    */ 
    public Object getAnnotation(Key sourceKey, Key destinationKey, Object property ){
    	if(sourceKey == null || destinationKey == null || property == null) throw new NullPointerException();
    	
    	if (!arcExists(sourceKey,destinationKey)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.containsKey(property)) throw new NoSuchElementException();
    		
    	
    	if(!annotationVertices.get(property).containsKey(sourceKey)) throw new NoSuchElementException();
    
    	if(!annotationVertices.get(property).containsKey(destinationKey)) throw new NoSuchElementException();
    	
    	if(!transpose){
    		return annotationVertices.get(property).get(sourceKey).outEdges.get(destinationKey).data;
    	}
    	else{
    		return annotationVertices.get(property).get(sourceKey).inEdges.get(destinationKey).data;
    	}
    }
   
     /**
     * Returns the value for the property that was removed indexed by the Key annotationKey a vertex with the Key vertexKey.
     * @return Returns the value for the property that was removed from the vertex.
     */ 
    
    public Object removeAnnotation(Key vertexKey,Object property) {
    	if(vertexKey == null || property == null) throw new NullPointerException();
    	
    	if (!annotationVertices.containsKey(property)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.get(property).containsKey(vertexKey)) throw new NoSuchElementException();
    	
    	Object save = annotationVertices.get(property).get(vertexKey).data;
    	
    	annotationVertices.get(property).remove(vertexKey);
    	
    	return save;
    }

    /**
    * Remove the value for the Property property of the arc with the with Source vertex sourceKey , Key destinationKey.
    *
    * @return  Returns the value for the Property property that was removed from the arc.   
    */ 
    public Object removeAnnotation( Key sourceKey, Key destinationKey, Object property ){
    	if(sourceKey == null || destinationKey == null || property == null) throw new NullPointerException();
    	
    	if (!arcExists(sourceKey,destinationKey)) throw new NoSuchElementException();
    	
    	if(!annotationVertices.containsKey(property)) throw new NoSuchElementException();
    		
    	
    	if(!annotationVertices.get(property).containsKey(sourceKey)) throw new NoSuchElementException();
    
    	if(!annotationVertices.get(property).containsKey(destinationKey)) throw new NoSuchElementException();
    	
    	if(!transpose){
    		Object save = annotationVertices.get(property).get(sourceKey).outEdges.get(destinationKey).data;
    		
    		annotationVertices.get(property).get(sourceKey).outEdges.remove(destinationKey);
    		annotationVertices.get(property).get(destinationKey).inEdges.remove(sourceKey);
    		
    		return save;
    		
    	}
    	else{
    		Object save = annotationVertices.get(property).get(sourceKey).inEdges.get(destinationKey).data;
    		
    		annotationVertices.get(property).get(sourceKey).inEdges.remove(destinationKey);
    		annotationVertices.get(property).get(destinationKey).outEdges.remove(sourceKey);
    		
    		return save;
    	}
    }
   

    /**
    *  Removes all values on vertices or arcs for the Property property. Use this to clean up between runs. 
    *	    
    */ 
    public void clearAnnotations(Object property) {
    	if(property == null) throw new NullPointerException();
    	
    	if (!annotationVertices.containsKey(property)) throw new NoSuchElementException();
    	
    	annotationVertices.remove(property);
    }
    
    /**
     * Returns the vertices in a Set
     * @return the vertices set
     */
    
    public Set<Key> returnVerticesSet(){
    	return vertices.keySet();
    }
    
    /**
     * This will return the adjacent vertices in a set
     * @param source the vertex 
     * @return the adjacent vertices to the given vertex
     */
    public Set<Key> returnAdjSet(Key source){
    	if(source == null) throw new NullPointerException();
    	
    	if(!vertices.containsKey(source)) throw new NoSuchElementException();
    	
    	if(!transpose)
    		return vertices.get(source).outEdges.keySet();
    	
    	else return vertices.get(source).inEdges.keySet();
    	
    	
    }
}
