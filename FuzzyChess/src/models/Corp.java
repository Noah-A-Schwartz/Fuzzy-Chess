package models;

import java.util.ArrayList;

/* Structure that contains members and leader of corp
 * Leaders: Left Bishop, Right Bishop, King
 * Left Bishop controls left 3 pawns and knight
 * Right Bishop controls right 3 pawns and knight
 * King controls Rooks, Queen, and middle 2 pawns
 * King can delegate control of its pieces to bishops
 * so essentially bishops can control all pieces excluding king
 * When Bishops die the king gains control of all its remaining pieces
 * However a subturn per turn is lost
 * When King dies its game over.
 */

//rework this class

public class Corp {
	private ArrayList<ChessPiece> members;
	private ChessPiece leader;
	private Corp kingsCorp;
	private boolean isActive;
	
	public Corp(Corp kingsCorp) {
		members = new ArrayList<ChessPiece>();
		this.kingsCorp = kingsCorp;
		isActive = true;
	}
	
	private Corp(ArrayList<ChessPiece> members, ChessPiece leader, boolean isActive) {
		this.members = members;
		this.leader = leader;
		this.kingsCorp = null;
		this.isActive = isActive;
	}
	
	public void setKingsCorp(Corp kc) {
		this.kingsCorp = kc;
	}
	
	public void addMember(ChessPiece member) {
		if(member.getid() == 'k' || member.getid() == 'K' || member.getid() == 'b' || member.getid() == 'B') {
			leader = member;
		}
		members.add(member);
	}
	
	//update to remove king corp member if captured
	public void removeMember(ChessPiece member) {
		if(member.getid() == leader.getid() && (kingsCorp != null)) {
			members.remove(member);
			transferMembers();
		}
		else {
			members.remove(member);
		}
	}
	
	public void removeAll() {
		while(!members.isEmpty()) {
			members.remove(0);
		}
	}
	
	//maybe redo - idk if we'll need a reference to the leader later
	public ChessPiece getLeader() {
		return leader;
	}
	
	//returns a reference to the corp specific members
	//used to 
	public void unfreezePieces(){
		for(ChessPiece c : members) {
			c.setHasMoved(false);
		}
	}
	
	/* get all active pieces of current corp
	 * active pieces are ones that haven't moved
	 * and are delegated from the kings corp */
	public ArrayList<ChessPiece> getActiveMembers() {
		ArrayList<ChessPiece> totalMembers = new ArrayList<ChessPiece>();
		//if not active then dont add delegated members
		if(isActive == false) {
			return members;
		}
		for(int i = 0; i < members.size(); i++) {
			if(!members.get(i).getHasMoved()) {
				totalMembers.add(members.get(i));
			}
		}
		//if null then this is the kings corp
		//otherwise king delegates all members besides himself to bishops
		if(kingsCorp != null) {
			for(int i = 0; i < kingsCorp.getActiveMembers().size(); i++) {
				if(!kingsCorp.getActiveMembers().get(i).equals(kingsCorp.getLeader())){
					totalMembers.add(kingsCorp.getActiveMembers().get(i));
				}
			}
		}
		return totalMembers;			
	}
	
	//used for board display (boardcolors) 
	public ArrayList<BoardPosition> getActiveMemberPositions(){
		ArrayList<BoardPosition> memberPositions = new ArrayList<BoardPosition>();
		for(ChessPiece member : getActiveMembers()) {
			memberPositions.add(member.getPosition());
		}
		return memberPositions;
	}
	
	
	//searches through personal and delegated members to
	//find a member that is at position p
	//use if selecting a friendly peice
	public ChessPiece getActiveMemberAt(BoardPosition p) {
		if(isActive) {
			for(ChessPiece member : getActiveMembers()) {
				if(member.getPosition().equals(p)) {
					return member;
				}
			}
		}
		return null;
	}

	//searches through personal members only to find
	//a member that is at position p - use if selecting
	//an enemy
	public ChessPiece getMemberAt(BoardPosition p) {
		for(ChessPiece member : members) {
			if(member.getPosition().equals(p)) {
				return member;
			}
		}
		return null;
	}
	
	//in case leader of bishop controlled forces dies
	//transfer all members back to king
	private void transferMembers() {
		for(int i = 0; i < members.size(); i++) {
			kingsCorp.addMember(members.get(i));
		}
		removeAll();
		isActive = false;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	//Need to set copied kings corp over when copying fuzzychess object
	public Corp copy() {
		ArrayList<ChessPiece> copiedMembers = new ArrayList<ChessPiece>();
		for(int i = 0; i < members.size(); i++) {
			copiedMembers.add(members.get(i).copy());
		}
		ChessPiece copiedLeader = leader.copy();
		return new Corp(copiedMembers, copiedLeader, isActive);		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(ChessPiece c : getActiveMembers()) {
			sb.append(c.getid() + " ");
		}
		sb.append("]");
		return sb.toString();		
	}
}
