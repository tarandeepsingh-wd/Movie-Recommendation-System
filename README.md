# Movie Recommendation System

A hybrid movie recommendation system built on the MovieLens 100K dataset. The project contains a Java prototype for generating recommendations and a Python notebook for model evaluation and comparison.

## Highlights

- Uses the full MovieLens 100K dataset instead of a toy catalog.
- Implements content-based, collaborative, hybrid, and matrix-factorization approaches.
- Compares models against simple baselines before claiming improvement.
- Reports both rating-prediction and top-N ranking metrics.
- Includes a configurable Java demo and an end-to-end Python evaluation notebook.

## Overview

This project demonstrates three recommendation approaches:

- Content-based filtering using movie genre vectors and cosine similarity
- User-based collaborative filtering using rating-pattern similarity
- Hybrid recommendation by combining content and collaborative scores

It also includes a Python notebook that evaluates baseline models and matrix factorization on the same dataset.

## Results

The notebook evaluates models on a held-out 80/20 train-test split using `random_state=42`.

| Model | Test RMSE | Improvement vs global mean |
|---|---:|---:|
| Global mean | 1.124 | baseline |
| Regularized bias model | 0.943 | 16% lower |
| User-based collaborative filtering | 0.934 | 17% lower |
| Matrix factorization | 0.922 | 18% lower |

For top-N ranking, the notebook also reports precision@10 and recall@10, and explains why a popularity baseline can outperform a rating-optimized model on MovieLens ranking metrics.

## Dataset

The project uses the MovieLens 100K dataset:

- 100,000 ratings
- 943 users
- 1,682 movies
- Movie metadata with genre labels

The Java prototype reads:

- `data/u.data` for user ratings
- `data/u.item` for movie metadata
- `data/u.genre` for genre names

## Project Structure

```text
.
+-- data/
|   +-- u.data
|   +-- u.genre
|   +-- u.info
|   +-- u.item
+-- src/
|   +-- CollaborativeRecommender.java
|   +-- ContentBasedRecommender.java
|   +-- DataLoader.java
|   +-- HybridRecommender.java
|   +-- Main.java
|   +-- Movie.java
|   +-- Similarity.java
|   +-- User.java
+-- movie_recommendation_mlss.ipynb
+-- PROJECT_SUMMARY.md
+-- requirements.txt
+-- README.md
```

## Java Prototype

The Java version is a lightweight recommendation engine. It does not require separate model training. It loads the dataset at runtime, builds similarity-based recommenders, and prints ranked recommendations.

### How It Works

1. `DataLoader` loads MovieLens movies, genres, and user ratings.
2. `ContentBasedRecommender` converts movie genres into vectors and recommends similar movies.
3. `CollaborativeRecommender` compares users with mean-centered rating similarity, keeps the strongest neighbors, and filters low-evidence recommendations.
4. `HybridRecommender` combines normalized content and collaborative scores using a weighted ranking formula.

### Implementation Notes

- Streams MovieLens files with buffered readers instead of loading full files into memory first.
- Uses deterministic ranking tie-breakers so repeated runs are stable.
- Applies minimum common-rating and neighbor-support thresholds in collaborative filtering.
- Normalizes collaborative predicted ratings before blending them with content similarity.

## Run the Java Prototype

From the repository root:

```bash
javac src/*.java
java -cp src Main
```

You can also pass custom demo parameters:

```bash
java -cp src Main <userId> <movieId> <topK> <alpha>
```

Example:

```bash
java -cp src Main 1 1 10 0.85
```

Expected output starts like this:

```text
Loaded 1682 movies and 943 users from data/
```

The program then prints:

- content-based recommendations for a sample movie
- collaborative recommendations for a sample user
- hybrid recommendations combining both methods

## Python Notebook

The notebook `movie_recommendation_mlss.ipynb` provides a deeper machine learning workflow:

- exploratory data analysis
- train/test split
- global mean baseline
- regularized bias model
- user-based collaborative filtering
- content-based and hybrid recommendation
- matrix factorization
- RMSE evaluation
- precision@10 and recall@10 ranking evaluation

Install the notebook dependencies:

```bash
pip install -r requirements.txt
```

Run Jupyter:

```bash
jupyter notebook movie_recommendation_mlss.ipynb
```

## Do You Need To Train The Model?

For the Java prototype: no separate training step is needed. It computes recommendations directly from the MovieLens files when the program runs.

For the Python notebook: yes, the notebook trains and evaluates models such as matrix factorization. That training is part of the notebook workflow.

## Key Learning Outcomes

- Built a recommender system using real user-item rating data
- Compared content-based and collaborative filtering approaches
- Combined multiple recommendation signals in a hybrid model
- Used baseline models before moving to matrix factorization
- Evaluated recommendations with both prediction and ranking metrics

## Limitations and Next Steps

- The Java recommender is an interpretable prototype, not a production service.
- The notebook uses a random train-test split for rating prediction; a timestamp split would better simulate future recommendations.
- Matrix factorization is optimized for RMSE, while real products usually need ranking-aware objectives.
- Natural next steps include BPR, implicit-feedback ALS, validation-based hyperparameter tuning, and richer metadata features.

## Tech Stack

- Java
- Python
- NumPy
- pandas
- scikit-learn
- matplotlib
- Jupyter Notebook

## MovieLens Credit

This project uses the MovieLens 100K dataset from GroupLens Research, University of Minnesota.

F. M. Harper and J. A. Konstan. "The MovieLens Datasets: History and Context." ACM Transactions on Interactive Intelligent Systems, 2015.
