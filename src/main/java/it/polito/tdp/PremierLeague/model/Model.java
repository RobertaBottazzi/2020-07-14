package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> grafo;
	private Map<Integer,Team> idMap;
	private PremierLeagueDAO dao;
	
	public Model() {
		this.dao=new PremierLeagueDAO();
		this.idMap=new HashMap<>();
		this.dao.loadAllTeams(idMap);
	}
	
	public void creaGrafo() {
		this.grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class); 
		Graphs.addAllVertices(this.grafo, idMap.values());
		for(Arco a: this.dao.getArchi(idMap)) {
			if(a.getPeso()==1)
				idMap.get(a.getT1().teamID).setPunti(idMap.get(a.getT1().teamID).getPunti()+3);
			else if(a.getPeso()==0) {
				idMap.get(a.getT1().teamID).setPunti(idMap.get(a.getT1().teamID).getPunti()+1);
				idMap.get(a.getT2().teamID).setPunti(idMap.get(a.getT2().teamID).getPunti()+1);
			}
			else /*(a.getPeso()==-1)*/
				idMap.get(a.getT2().teamID).setPunti(idMap.get(a.getT2().teamID).getPunti()+3);
		}
		for(Arco a: this.dao.getArchi(idMap)) {
			if(this.grafo.containsVertex(a.getT1()) && this.grafo.containsVertex(a.getT2())) {
				Team primo=a.getT1();
				Team ultimo=a.getT2();
				int peso=idMap.get(primo.teamID).getPunti()-idMap.get(ultimo.teamID).getPunti();
				if(peso>0)
					Graphs.addEdgeWithVertices(this.grafo, primo, ultimo, peso);
				else if(peso<0)
					Graphs.addEdgeWithVertices(this.grafo, ultimo, primo, Math.abs(peso));
			}
		}
	}
	
	public Collection<Team> getSquadreBattute(Team team){
		Map<Integer,Team> result= new TreeMap<>();
		for(Team t: Graphs.successorListOf(this.grafo, team))
			result.put((int) this.grafo.getEdgeWeight(this.grafo.getEdge(team, t)), t);
		return result.values();		
	}
	
	public Collection<Team> getSquadreHannoBattuto(Team team){
		Map<Integer,Team> result= new TreeMap<>();
		for(Team t: Graphs.predecessorListOf(this.grafo, team))
			result.put((int) this.grafo.getEdgeWeight(this.grafo.getEdge(t, team)), t);
		return result.values();		
	}
	
	public SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> getGrafo() {
		return this.grafo;
	}
}


