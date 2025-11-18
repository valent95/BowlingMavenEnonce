package bowling;

public class PartieMonoJoueur {

	private int NOMBRE_QUILLES = 10;
	private int NOMBRE_TOURS = 10;
	private final int[][] lancers; // lancers[tour][lancer]
	private int tourCourant;
	private int lanceProchainDansTour;

	public PartieMonoJoueur() {
		lancers = new int[NOMBRE_TOURS][3];
		tourCourant = 1;
		lanceProchainDansTour = 1;
	}

	public int getNombreQuilles() {
		return NOMBRE_QUILLES;
	}

	public int getNombreTours() {
		return NOMBRE_TOURS;
	}

	/**
	 * Cette méthode doit être appelée à chaque lancer de boule
	 *
	 * @param nombreDeQuillesAbattues le nombre de quilles abattues lors de ce lancer
	 * @throws IllegalStateException si la partie est terminée
	 * @return vrai si le joueur doit lancer à nouveau pour continuer son tour, faux sinon	
	 */
	public boolean enregistreLancer(int nombreDeQuillesAbattues) {
		if (estTerminee()) {
			throw new IllegalStateException("La partie est terminée");
		}

		int idxTour = tourCourant - 1;
		lancers[idxTour][lanceProchainDansTour - 1] = nombreDeQuillesAbattues;

		boolean tourContinue;

		if (tourCourant < NOMBRE_TOURS) {
			// Tours normaux (1-9)
			if (lanceProchainDansTour == 1) {
				// Premier lancer du tour
				tourContinue = (nombreDeQuillesAbattues != NOMBRE_QUILLES);
			} else {
				// Deuxième lancer du tour: fin du tour de toute façon
				tourContinue = false;
			}
		} else {
			// Dernier tour (tour 10)
			switch (lanceProchainDansTour) {
				case 1:
					// Premier lancer du tour 10: il y aura toujours un 2e lancer
					tourContinue = true;
					break;
				case 2: {
					// Deuxième lancer du tour 10
					int premierLancer = lancers[idxTour][0];
					// Si strike au premier OU spare (somme = 10), droit à un 3e lancer
					tourContinue = (premierLancer == NOMBRE_QUILLES) || (premierLancer + nombreDeQuillesAbattues == NOMBRE_QUILLES);
					break;
				}
				default:
					// Troisième lancer du tour 10: fin du tour
					tourContinue = false;
			}
		}

		if (tourContinue) {
			lanceProchainDansTour++;
		} else {
			// Passer au tour suivant
			tourCourant++;
			lanceProchainDansTour = 1;

			if (tourCourant > NOMBRE_TOURS) {
				tourCourant = 0;
			}
		}

		return tourContinue;
	}

	/**
	 * Cette méthode donne le score du joueur.
	 * Si la partie n'est pas terminée, on considère que les lancers restants
	 * abattent 0 quille.
	 * @return Le score du joueur
	 */
	public int score() {
		int score = 0;

		for (int tour = 0; tour < NOMBRE_TOURS; tour++) {
			int premier = lancers[tour][0];

			if (tour < NOMBRE_TOURS - 1) {
				// Tours normaux (1-9)
				if (premier == NOMBRE_QUILLES) {
					// Strike: 10 + 2 lancers suivants
					score += NOMBRE_QUILLES + getDeuxLancersSuivants(tour);
				} else {
					int deuxieme = lancers[tour][1];
					if (premier + deuxieme == NOMBRE_QUILLES) {
						// Spare: 10 + 1 lancer suivant
						score += NOMBRE_QUILLES + getLancerSuivant(tour);
					} else {
						// Coup normal
						score += premier + deuxieme;
					}
				}
			} else {
				// Tour 10: tous les lancers du tour 10 comptent directement
				score += premier + lancers[tour][1] + lancers[tour][2];
			}
		}

		return score;
	}

	/**
	 * Récupère le lancer suivant celui du tour spécifié
	 */
	private int getLancerSuivant(int tour) {
		if (tour == NOMBRE_TOURS - 1) {
			// Pas de tour après le tour 10
			return 0;
		}

		int tourSuivant = tour + 1;
		return lancers[tourSuivant][0];
	}

	/**
	 * Récupère les 2 lancers suivants celui du tour spécifié
	 */
	private int getDeuxLancersSuivants(int tour) {
		if (tour == NOMBRE_TOURS - 1) {
			// Pas de tour après le tour 10
			return 0;
		}

		int tourSuivant = tour + 1;
		int premier = lancers[tourSuivant][0];

		if (tourSuivant == NOMBRE_TOURS - 1) {
			// Si le tour suivant est le dernier, on prend ses 2 premiers lancers
			return premier + lancers[tourSuivant][1];
		} else if (premier == NOMBRE_QUILLES) {
			// Strike au tour suivant: on prend le strike et le premier du tour d'après
			return premier + lancers[tourSuivant + 1][0];
		} else {
			// Pas de strike: on prend le premier et le deuxième du tour suivant
			return premier + lancers[tourSuivant][1];
		}
	}

	/**
	 * @return vrai si la partie est terminée pour ce joueur, faux sinon
	 */
	public boolean estTerminee() {
		return tourCourant == 0;
	}

	/**
	 * @return Le numéro du tour courant [1..10], ou 0 si le jeu est fini
	 */
	public int numeroTourCourant() {
		return tourCourant;
	}

	/**
	 * @return Le numéro du prochain lancer pour tour courant [1..3], ou 0 si le jeu
	 *         est fini
	 */
	public int numeroProchainLancer() {
		if (estTerminee()) {
			return 0;
		}
		return lanceProchainDansTour;
	}

}
