package networkmetrics;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Driver<Key extends Comparable<Key>>{

   public static <Key extends Comparable<Key>> void main(String[] args){
      try{
      HashMapGraph G = new HashMapGraph();
         
         String fileName = args[0];
         String currentScan; 
         String nodeProp;
         String edgeProp;
         
         Scanner scan = new Scanner(new File(args[0]));
         
         //BEGIN INSERTING NODES
         
         String current = scan.nextLine();
         if(current.equals("*node data ") 
         || current.equals("*Node properties") 
         || current.equals("*Node data"))
         {
            nodeProp = scan.nextLine();
         }
         else{ System.out.println("Invalid VNA file"); }
         
         currentScan = "nodes";
         
         while(scan.hasNextLine() && currentScan == "nodes"){
            current = scan.nextLine();
            if(current.equals("*tie data ") || current.equals("*Tie data")){ currentScan = "edges"; }//ENDS LOOP AND MOVES ON TO EDGES.
            else{
               String[] split = current.split("\\s+");
               G.insertVertex(split[0]);
            }  
         }
         
         //BEGIN INSERTING EDGES
         
         edgeProp = scan.nextLine();
         
         while(scan.hasNextLine()){ //INSERTS ALL EDGES (key1,key2,null data,weight given)
            current = scan.nextLine();
            String[] split = current.split("\\s+");
            if(split.length == 2){
               G.insertArc(split[0],split[1]);
            }else{
               G.insertArc(split[0],split[1],split[2]);
            }
         }
         
         //INDEGREE CALCULATION
         
         double inmin = 1000000.0; 
         double intotal= 0;
         double inmax = 0; 
         Iterator vertices = G.vertices();
         while(vertices.hasNext()){
            Key o = (Key)vertices.next();
            double degree = G.inDegree(o);
            if(degree > inmax){
               inmax = degree;
            }
            if(degree < inmin){
               inmin = degree;
            }
            intotal = intotal + degree;
         }
         double inaverage = intotal/G.vertexCount();
      
      
         //OUTDEGREE CALCULATION
         
         double outmin = 1000000.0; 
         double outtotal= 0;
         double outmax = 0; 
         vertices = G.vertices();
         while(vertices.hasNext()){
            Key o = (Key)vertices.next();
            double degree = G.outDegree(o);
            if(degree > outmax){
               outmax = degree;
            }
            if(degree < outmin){
               outmin = degree;
            }
            outtotal = outtotal + degree;            
         }
         double outaverage = outtotal/G.vertexCount();
         
         //GRAPH DENSITY
         
          double density = ((double)G.arcCount()/((double)G.vertexCount()*((double)G.vertexCount()-1)));
         
         //RECIPROCITY
         
         int rec =0;
         Iterator arcs = G.arcs();
         while(arcs.hasNext()){
            ArrayList<Key> list = (ArrayList<Key>)arcs.next();
            if(G.arcExists(list.get(1),list.get(0))){
               rec++;
            }   
         }
         double reciprocity = (double)rec/G.arcCount();
         
         //DEGREE CORRELATION
         
         //               S1SE - (S2)^2
         //     r  =     ---------------  
         //               S1S3 - (S2)^2   
         
         BigDecimal Se = new BigDecimal(0);
         arcs = G.arcs(); //restarts arcs iterator
         
         while(arcs.hasNext()){
            ArrayList<Key> list = (ArrayList<Key>)arcs.next();
            
            //SE
            double degreeUin = G.inDegree(list.get(0));
            double degreeUout = G.outDegree(list.get(0));
            double degreeVin = G.inDegree(list.get(1));
            double degreeVout = G.outDegree(list.get(1));
            
            double degreeU = degreeUin + degreeUout;
            double degreeV = degreeVin + degreeVout;
            BigDecimal x = new BigDecimal(degreeU*degreeV);
            Se =Se.add(x);
            
        }
        Se =Se.multiply(new BigDecimal(2));
            
        //S1
        
        BigDecimal S_one = new BigDecimal(0);
        vertices = G.vertices();
        while(vertices.hasNext()){
           Key o = (Key)vertices.next();
           BigDecimal x = new BigDecimal(G.inDegree(o)+G.outDegree(o));
           S_one = S_one.add(x);
        }
            
        //S2
        
        BigDecimal S_two = new BigDecimal(0);
        vertices = G.vertices();
        while(vertices.hasNext()){
           Key o = (Key)vertices.next();
           BigDecimal x = new BigDecimal(G.inDegree(o)+G.outDegree(o));
           BigDecimal y = x.multiply(x);
           S_two = S_two.add(y);
           
        }
            
        //S3
        
        BigDecimal S_three = new BigDecimal(0);
        vertices = G.vertices();
        while(vertices.hasNext()){
           Key o = (Key)vertices.next();
           BigDecimal x = new BigDecimal(G.inDegree(o)+G.outDegree(o));
           BigDecimal y = (x.multiply(x)).multiply(x);//
           S_three = S_three.add(y);
        }
            
         //               S1SE - (S2)^2
         //     r  =     ---------------  
         //               S1S3 - (S2)^2    
            
         BigDecimal S1_times_SE = S_one.multiply(Se);
         BigDecimal S1_times_S3 = S_one.multiply(S_three);
         BigDecimal S22 = S_two.multiply(S_two);    
                        
                                           //(S1*SE)      -   (S2*S2)      /            S1*S3    -         S2*S2
         BigDecimal degreeCorrelation = ((   S1_times_SE.subtract(S22)  ).divide(   (S1_times_S3.subtract(S22)),2,RoundingMode.HALF_UP));
         
         
         //CLUSTERING COEFFICIENT
         
            //NUMBER OF TRIANGLES
         BigDecimal not = new BigDecimal(0);
         arcs = G.arcs();
         
         while(arcs.hasNext()){
            ArrayList<Key> list = (ArrayList<Key>)arcs.next();  
            if(list.get(0) != list.get(1)){
               BigDecimal one = new BigDecimal(1);
               not = not.add(one);
            }         
         }
         
            //NUMBER OF CONNECT TRIPLES
         BigDecimal noct = new BigDecimal(0);
         vertices = G.vertices();
         while(vertices.hasNext()){
            Key o = (Key)vertices.next();
            BigDecimal degV = new BigDecimal(G.inDegree(o)+G.outDegree(o));
            BigDecimal half = new BigDecimal(.5);
            BigDecimal one = new BigDecimal(1);
            noct = noct.add((degV.multiply(half)).multiply((degV.subtract(one))));
          }
         
         BigDecimal cluster = (not.multiply(new BigDecimal(3))).divide(noct,2,RoundingMode.HALF_UP );
         
         //GEODESIC 
         
         BigDecimal totalpath = new BigDecimal(0);
         BigDecimal totallength = new BigDecimal(0);
         BigDecimal maxpath = new BigDecimal(0);
         
         vertices = G.vertices();
         Iterator vertices2 = G.vertices();
         
         while(vertices.hasNext()){
            
            //BFS
            Key o = (Key)vertices.next(); //S
            vertices2 = G.vertices();
            while(vertices2.hasNext()){
                  
               Key o2 = (Key)vertices2.next();
               G.setAnnotation(o2,"COLOR","WHITE"); 
               G.setAnnotation(o2,"D","INFINITY");
               G.setAnnotation(o2,"PARENT","NIL");
            }
               G.setAnnotation(o,"COLOR","GRAY"); 
               G.setAnnotation(o,"D",0);
               G.setAnnotation(o,"PARENT","NIL");            
            Queue<Key> Q = new LinkedList<Key>();
            Q.add(o);
            while(Q.peek() != null){
               Key x = Q.poll();
               
               Iterator outV = G.outAdjacentVertices(x);
               while(outV.hasNext()){
                  ArrayList<Key> list = (ArrayList<Key>)outV.next();
                  
                  Key z = list.get(1);
                  if(G.getAnnotation(z,"COLOR").equals("WHITE")){
                       G.setAnnotation(z,"COLOR","GRAY"); 
                       G.setAnnotation(z,"D",(Integer)(G.getAnnotation(x,"D"))+1);
                       
                       BigDecimal pathlength = new BigDecimal((Integer)G.getAnnotation(z,"D"));
                       BigDecimal one = new BigDecimal(1);
                       totalpath = totalpath.add(pathlength); //Adds all "d"
                       totallength = totallength.add(one); //increments by one for every d
                       if(pathlength.compareTo(maxpath) > 0){
                           maxpath = pathlength;
                       }
                                              
                       G.setAnnotation(z,"PARENT",x);  
                       Q.add(z);                   
                  }
                  G.setAnnotation(x,"COLOR","BLACK");
               }
            }
         }
         
         

         
         System.out.println("-----------------------------------------------");
         System.out.println("    Graph   <"+fileName+">");
         System.out.println("-----------------------------------------------");
         System.out.println("|V| = "+G.vertexCount());
         System.out.println("|E| = "+G.arcCount());
         System.out.println("Density ~= "+density);
         System.out.println("INDEGREE: minimum~= "+inmin);
         System.out.println("INDEGREE: maximum~= "+inmax);
         System.out.println("INDEGREE: average~= "+inaverage);
         System.out.println("OUTDEGREE: minimum~= "+outmin);
         System.out.println("OUTDEGREE: maximum~= "+outmax);
         System.out.println("OUTDEGREE: average~= "+outaverage);
         System.out.println("Reciprocity percentage: ~= "+ reciprocity);
         System.out.println("Undirected Degree Correlation: ~= "+ degreeCorrelation);
         System.out.println(" (!Inaccurate) Clustering coefficient: ~= "+ cluster);
         System.out.println("Mean Geodesic Path ~= "+ totalpath.divide(totallength,2,RoundingMode.HALF_UP));
         System.out.println("Directed Diameter~= "+ maxpath);
         
      }
      catch (IOException o){ System.out.println("ERROR: "+o.getMessage()); }
   }
}
