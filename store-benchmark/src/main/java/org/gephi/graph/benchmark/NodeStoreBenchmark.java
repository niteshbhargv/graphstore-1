/*
 * Copyright 2012-2013 Gephi Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gephi.graph.benchmark;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.Random;
import org.gephi.attribute.api.Column;
import org.gephi.attribute.api.Origin;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.store.ColumnImpl;
import org.gephi.graph.store.EdgeImpl;
import org.gephi.graph.store.EdgeStore;
import org.gephi.graph.store.NodeImpl;
import org.gephi.graph.store.NodeStore;


/**
 *
 * @author mbastian, niteshbhargv
 */
public class NodeStoreBenchmark {

    private static int NODES_READ = 500000;
    private static int NODES_WRITE = 50000;
    private Object object;

    public Runnable iterateStore() {
        final NodeStore nodeStore = new NodeStore();
        int nodes = NODES_READ;
        for (int i = 0; i < nodes; i++) {
            NodeImpl n = new NodeImpl(String.valueOf(i));
            nodeStore.add(n);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                Iterator<Node> m = nodeStore.iterator();
                for (; m.hasNext();) {
                    NodeImpl b = (NodeImpl) m.next();
                    object = b;
                }
            }
        };
        return runnable;
    }

    public Runnable resetNodeStore() {
        int nodes = NODES_WRITE;
        final NodeStore nodeStore = new NodeStore();
        final NodeImpl[] nodeArray = new NodeImpl[nodes];
        for (int i = 0; i < nodes; i++) {
            NodeImpl n = new NodeImpl(String.valueOf(i));
            nodeStore.add(n);
            nodeArray[i] = n;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < nodeArray.length; i++) {
                    NodeImpl n = nodeArray[i];
                    nodeStore.remove(n);
                }
                for (int i = 0; i < nodeArray.length; i++) {
                    NodeImpl n = nodeArray[i];
                    nodeStore.add(n);
                }
            }
        };
        return runnable;
    }

    public Runnable pushStore() {
        final NodeImpl[] nodeStock = new NodeImpl[NODES_WRITE];
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < NODES_WRITE; i++) {
                    nodeStock[i] = new NodeImpl(String.valueOf(i));
                }
                int nodes = NODES_WRITE;
                NodeStore nodeStore = new NodeStore();
                for (int i = 0; i < nodes; i++) {
                    NodeImpl n = nodeStock[i];
                    nodeStore.add(n);
                }
                object = nodeStore;
            }
        };
        return runnable;
    }
    
    /**
     * addNode - creates a random graph and adds a node into the graph 
     */
    
    public Runnable addNode(final int n , final double p) {
       
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                RandomGraph randomGraph = new RandomGraph(n,p);                
                NodeImpl newRNode = new NodeImpl(String.valueOf(randomGraph.numberOfNodes+1));
                randomGraph.graphStore.addNode(newRNode);
                Random random = new Random();
                //edges being added taking wiring probability into the consideration
                if(randomGraph.wiringProbability > random.nextDouble())
                {
                    randomGraph.setEdgeCount(randomGraph.getEdgeCount()+1);
                    int temp = random.nextInt(randomGraph.numberOfNodes);
                  
                    NodeImpl source = randomGraph.graphStore.getNodeStore().get(String.valueOf(temp));
                    EdgeImpl Edge = new EdgeImpl(String.valueOf(randomGraph.getEdgeCount()), source,newRNode, 0, 1.0, true);
                    if(source!=newRNode){
                        randomGraph.graphStore.getEdgeStore().add(Edge);
                     
                    }
                    
                }
               
            }
        };
        return runnable;
        
    }
    
    /**
     * removeNode - creates a random graph and randomly removes a node from the graph 
     */
    
    public Runnable removeNode(final int n, final double p) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Random random = new Random();                
               
                RandomGraph randomGraph = new RandomGraph(n,p);
                int nodeID = random.nextInt(randomGraph.numberOfNodes);
                NodeImpl node = randomGraph.graphStore.getNodeStore().get(String.valueOf(nodeID));
                Column col = randomGraph.nodeColumnStore.getColumn("id");
                node.removeAttribute(col);  // remove the attribute from the removed node
                
                randomGraph.graphStore.clearEdges(node);    // clears the edges associated with the random graph
                randomGraph.graphStore.removeNode(node);
                
                
                
            }
            
        };
        return runnable;
}
    /**
     * iterateNodes - creates a random graph and iterates inside the given nodes 
     */
   public Runnable iterateNodes(final int n , final double p){
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
               
                RandomGraph randomGraph = new RandomGraph(n,p); 
                Iterator<Node> m = randomGraph.graphStore.getNodeStore().iterator();
                for (; m.hasNext();) {
                    NodeImpl b = (NodeImpl) m.next();
                    object = b;
                }              
            }
        };
        
        return runnable;
       
   }
   
   /**
     * iterateNeighbors - creates a random graph and iterates inside the given node's neighbors 
     */
   
   public Runnable iterateNeighbors(final int n , final double p){
       Runnable runnable = new Runnable() {

           @Override
           public void run() {
            RandomGraph randomGraph = new RandomGraph(n,p); 
               Random random = new Random();
               NodeImpl node = randomGraph.graphStore.getNodeStore().get(random.nextInt(randomGraph.numberOfNodes));
               Iterator<Node> Neighbors = randomGraph.graphStore.getEdgeStore().neighborIterator(node);
               }
       };
       return runnable;
   }
   
   /**
     * addKleinbergNode - creates a Kleinberg graph and adds nodes with local contacts and long range contacts  
     */
   
   public Runnable addKleinbergNode(final int no,final int local,final int Long)
   {
       Runnable runnable = new Runnable() {

           @Override
           public void run() {
               // Adding the Node
               Kleinberg graph = new Kleinberg(no,local,Long);
               Random random = new Random();
               NodeImpl newNode = new NodeImpl(String.valueOf(graph.getNodeCount()+1));
               graph.setNodeCount(graph.getNodeCount()+1);
               int p = graph.getp();
               int q = graph.getq();
               int n = graph.getn();
               int r = graph.getr();
               
             
               int temp = 0;
               
               NodeImpl nodes[][] =new NodeImpl[n][n];
               graph.graphstore.addNode(newNode);
               for(int it=0;it<n;it++)
                   for(int j=0;j<n;j++)
                   {
                       nodes[it][j] = graph.graphstore.getNode(temp);
                       
                       temp++;
                   }
                       
               
               
               

               // add edges that has this node as a source or destination and have atmost p local contacts
               for (int i=0;i<n; i++)
               for (int j = 0; j < n ; ++j)
               {
                		for (int k = i - p; k <= i + p ; ++k)
                                {
                                     for (int l = j - p; l <= j + p ; ++l)
                                        {
                                            
                                       
						if ((graph.isTorusBased() || !graph.isTorusBased() && k >= 0  && l >= 0 ) &&
                                                         graph.d(i, j, k, l) <= p  )
                                                {
							
                                                        Object id  = nodes[(k + n) % n][(l + n) % n].getId();
                                                        graph.setEdgeCount(graph.getEdgeCount()+1);
                                                        EdgeImpl Edge = new EdgeImpl(String.valueOf(graph.getEdgeCount()),newNode,graph.graphstore.getNode(id),0,1.0,true);
                                                        graph.getIdSet().add(Edge.getLongId());
                                                        graph.graphstore.addEdge(Edge);
                                                        
						}
						
					}
              
                                }
               }
               
               // add edges that has this node as a source or destination and have atmost q local contacts
                 for(int i=0;i<n;i++)
			for (int j = 0; j < n ; ++j)
                        {
                            
				double sum = 0.0;
				for (int k = 0; k < n ; ++k)
					for (int l = 0; l < n ; ++l) 
                                        {
						if ( !graph.isTorusBased() && graph.d(i, j, k, l) > p)
							sum += Math.pow(graph.d(i, j, k, l), -r);
						else if ( graph.isTorusBased() && graph.dtb(i, j, k, l) > p)
							sum += Math.pow(graph.dtb(i, j, k, l), -r);
						
					}
                            
				for (int m = 0; m < q ; ++m) 
                                {
                                    
					double  b = random.nextDouble();
					boolean e = false;
					while (!e) 
                                        {
                                             
						double pki = 0.0;
						for (int k = 0; k < n && !e ; ++k)
							for (int l = 0; l < n && !e; ++l)
								if (!graph.isTorusBased() && graph.d(i, j, k, l) > p || graph.isTorusBased() && graph.dtb(i, j, k, l) > p)
                                                                {
									
                                                                        pki += Math.pow(!graph.isTorusBased() ? graph.d(i, j, k, l) : graph.dtb(i, j, k, l), -r) / sum;
                                                                        Object id  = nodes[k][l].getId();
                                                                        EdgeImpl Edge = new EdgeImpl(String.valueOf(graph.getEdgeCount()+1),newNode,graph.graphstore.getNode(id),0,1.0,true);
									 
                                                                               graph.getIdSet().add(graph.getEdgeCount()+1);
                                                                               									
										graph.graphstore.addEdge(Edge);
                                                                                
										e = true;
				
								}
						b = random.nextDouble();
					}
					
				}
			}

               
           }
       };
       
       return runnable;
   }
   
   /**
     * RemoveKleinbergNode - creates a Kleinberg graph and removes nodes (picks one at random) with local contacts and long range contacts  
     */
   
   
public Runnable RemoveKleinbergNode(final int no,final int local,final int Long){
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Kleinberg graph = new Kleinberg(no,local,Long);
            Random random = new Random();
            int nodeID = random.nextInt(graph.getNodeCount());
            graph.graphstore.clearEdges(graph.graphstore.getNode(nodeID));
            graph.graphstore.removeNode(graph.graphstore.getNode(nodeID));
            
        }
    };
        return runnable;
}


  /**
     * iterateKleinbergNodes - creates a Kleinberg graph and iterates throughout nodes with local contacts and long range contacts  
     */  

public Runnable iterateKleinbergNodes(final int no,final int local,final int Long){
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Kleinberg graph = new Kleinberg(no,local,Long);
                NodeStore nodeStore = new NodeStore();
                nodeStore = (NodeStore) graph.graphstore.getNodes();
                Iterator<Node> m = nodeStore.iterator();
                for (; m.hasNext();) {
                    NodeImpl b = (NodeImpl) m.next();
                    object = b;
                }              
            }
        };
        
        return runnable;
       
   }


/**
  * addAttrRandomGraph - creates a random graph and gets the node's attributes. 
  */

public Runnable addAttrRandomGraph(final int n, final double p){
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
             
            RandomGraph randomGraph = new RandomGraph("id",n,p);
             Column col = randomGraph.nodeColumnStore.getColumn("id");
             for(Node n:randomGraph.graphStore.getNodes())
             {
                 
                 object =   n.getAttribute(col);
              
             }
               
             
        }
    };
    return runnable;
}

   
}