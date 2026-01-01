package io.github.eddie999.minesweepergame.block;

public interface CustomBlockAnimation {
	int getState( CustomMaterial material);
	int getState();
	void setState( CustomMaterial material);
	void setState( int state);
	int stateNum();
}
