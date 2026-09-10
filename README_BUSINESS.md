# Recommendation Business Behavior

## 1. Recommendation objective

The engine recommends active videos that are relevant to a user's interests,
while balancing personal affinity, watch behavior, popularity, recency, and
engagement. A recommendation response should not repeat videos the user has
already interacted with and should remain diverse when enough candidates are
available.

## 2. Interaction signals and weights

`PreferenceProfile` transforms interaction history into signals of interest.
The base interaction weights are:

| Interaction | Weight |
|---|---:|
| `LIKE` | `3.0` |
| `SHARE` | `2.0` |
| `COMMENT` | `2.0` |
| `VIEW` | `1.0` |
| `SKIP` | `-0.5` |
| `DISLIKE` | `-2.0` |

Watched duration modifies the interaction weight. Positive interactions build
affinity, while `DISLIKE` marks a video for exclusion.

## 3. Watch ratio

Each interaction calculates:

```text
watchRatio = watchDuration / videoDuration
```

The result is capped at `1.0`.

Example:

```text
Video duration: 100 seconds
Watched duration: 75 seconds
watchRatio: 0.75
```

For `VIEW` and `SKIP`, the weight depends directly on the ratio. For `LIKE`,
`SHARE`, `COMMENT`, and `DISLIKE`, interaction strength is combined with the
watched duration.

## 4. Preference profile

The profile contains:

- Affinity for categories
- Affinity for tags
- Rejected videos
- Videos already watched
- Watch ratio by category
- Watch ratio by tag

The profile is built from the user's recent interaction history and supplies
the personalized signals used during candidate generation and scoring.

## 5. Candidate generation strategies

`CandidateGenerator` allows independent strategies to be defined:

- `PersonalizedCandidateGenerator`: prioritizes category and tag affinity.
- `TagCandidateGenerator`: prioritizes tag matches.
- `PopularCandidateGenerator`: sorts by popularity.
- `RecentCandidateGenerator`: sorts by creation date.

`RecommendationEngine` runs the strategies and combines their results without
duplicating videos. Introducing a new strategy does not require modifying the
engine.

## 6. Scoring formula

`RecommendationScorer` centralizes the scoring formula:

```text
score =
    categoryAffinity * 0.25 +
    tagAffinity * 0.20 +
    watchScore * 0.20 +
    popularityScore * 0.10 +
    recencyScore * 0.10 +
    engagementScore * 0.15
```

The components are:

- `categoryAffinity`: whether the category matches the user's interests.
- `tagAffinity`: the proportion of matching tags.
- `watchScore`: historical watch ratio for categories and tags.
- `popularityScore`: stored popularity of the video.
- `recencyScore`: score that decays with the video's age.
- `engagementScore`: overall interaction level of the video.

Recency uses:

```text
recencyScore = exp(-ageInDays / 30)
```

## 7. Filtering and diversity

`VideoRanker`:

1. Removes inactive videos.
2. Excludes videos marked with `DISLIKE`.
3. Excludes videos already interacted with.
4. Sorts by descending score.
5. Uses the identifier as a deterministic tie-breaker.
6. Limits repetition by creator and category.

The current configuration attempts to maintain a maximum of two videos per
creator and per category. If there are not enough diverse candidates, the
remaining candidates complete the response.

## 8. Cold start

When the user has no history:

- No personalized affinities exist.
- Popularity, recency, and engagement gain relative weight.
- Popular and recent candidates are combined.
- Creator and category diversity is maintained.
- No duplicates are returned.

This provides useful recommendations to new users without a previously built
profile.

## 9. End-to-end recommendation flow

For `GET /api/v1/users/user-1/recommendations?limit=20`, the business flow is:

1. Validate that the requested limit is between 1 and 100.
2. Load the user and the latest 500 interactions.
3. Identify videos already interacted with.
4. Generate a paginated candidate set.
5. Load videos related to the user's history.
6. Build the `PreferenceProfile`.
7. Run `RecommendationEngine` and its candidate strategies.
8. Score and rank candidates with `RecommendationScorer` and `VideoRanker`.
9. Return the recommendations in sorted, diverse order.

## 10. Concrete example

Assume a user frequently watches Java and backend videos, completes most of
them, likes one Java video, and skips unrelated frontend videos. The profile
therefore has positive Java/backend category and tag affinity, high watch
ratios for those interests, and a negative signal for the skipped content.

For a candidate with these normalized components:

```text
categoryAffinity = 0.90
tagAffinity      = 0.50
watchScore       = 0.80
popularityScore  = 0.70
recencyScore     = 0.60
engagementScore  = 0.50
```

The score is:

```text
0.90 * 0.25 + 0.50 * 0.20 + 0.80 * 0.20
+ 0.70 * 0.10 + 0.60 * 0.10 + 0.50 * 0.15
= 0.69
```

The candidate is removed if it is inactive, disliked, or already interacted
with. Otherwise it competes with other candidates, and the final ranking
limits repeated creators and categories to preserve diversity.

For the interaction endpoint, a client can record the signal used to update
the profile:

```http
POST /api/v1/users/user-1/interactions
Content-Type: application/json
```

```json
{
  "videoId": "video-java26",
  "type": "LIKE",
  "watchDuration": "PT15M"
}
```

For technical persistence, configuration, and error handling, see
[`README_ARCHITECTURE.md`](README_ARCHITECTURE.md).
