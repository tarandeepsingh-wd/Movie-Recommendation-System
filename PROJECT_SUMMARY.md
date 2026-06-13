# Project Summary

## Objective

Build and evaluate a movie recommendation system on MovieLens 100K using both traditional recommendation-system methods and a stronger matrix-factorization baseline.

## Dataset

- MovieLens 100K
- 100,000 user ratings
- 943 users
- 1,682 movies
- 19 movie genre features

## Methods Implemented

### Content-Based Filtering

Movies are represented as genre vectors. Recommendations are ranked by cosine similarity between the selected movie and every other movie.

### User-Based Collaborative Filtering

Users are compared using mean-centered rating similarity. The Java prototype keeps the strongest neighbors, filters low-evidence recommendations, and predicts candidate movies from neighbor-adjusted ratings.

### Hybrid Ranking

The hybrid recommender blends content similarity and collaborative predicted rating signals. Collaborative ratings are normalized before blending so the score scales are comparable.

### Matrix Factorization

The Python notebook trains a Funk SVD-style matrix factorization model with user and item latent factors, user/item biases, L2 regularization, and stochastic gradient descent.

## Evaluation

The notebook uses an 80/20 train-test split with `random_state=42`.

| Model | Test RMSE | Improvement vs global mean |
|---|---:|---:|
| Global mean | 1.124 | baseline |
| Regularized bias model | 0.943 | 16% lower |
| User-based collaborative filtering | 0.934 | 17% lower |
| Matrix factorization | 0.922 | 18% lower |

The notebook also evaluates top-N recommendation quality with precision@10 and recall@10.

## Key Takeaways

- Matrix factorization gives the strongest rating-prediction performance.
- Content-based filtering remains useful for item-to-item recommendations and cold-start style scenarios.
- Popularity can be difficult to beat on top-N ranking metrics, which is why ranking-aware methods such as BPR or implicit ALS are natural next steps.
- A useful recommender needs both evaluation discipline and product-aware trade-off thinking.

## Reproducibility

Run the Java prototype:

```bash
javac src/*.java
java -cp src Main
```

Run a configurable Java demo:

```bash
java -cp src Main 1 1 10 0.85
```

Run the notebook:

```bash
pip install -r requirements.txt
jupyter notebook movie_recommendation_mlss.ipynb
```

## Next Steps

- Add ranking-aware training with BPR or implicit-feedback ALS.
- Tune hyperparameters with a validation split.
- Add timestamp-aware evaluation to simulate future recommendation behavior.
- Add richer metadata features such as release year and item popularity priors.
