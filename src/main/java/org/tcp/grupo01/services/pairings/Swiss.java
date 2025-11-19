package org.tcp.grupo01.services.pairings;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Swiss<T extends Competitor> implements Pairing<T> {

    private final int maxWins;
    private final int maxLosses;
    private final BiFunction<T, T, Match<T>> createMatch;

    public Swiss(int maxWins, int maxLosses, BiFunction<T, T, Match<T>> createMatch) {
        this.maxWins = maxWins;
        this.maxLosses = maxLosses;
        this.createMatch = createMatch;
    }

    private Map<T, Set<T>> buildHistory(List<Match<T>> matches) {
        Map<T, Set<T>> map = new HashMap<>();

        for (Match<T> m : matches) {
            T a = m.getCompetitorA();
            T b = m.getCompetitorB();

            map.computeIfAbsent(a, k -> new HashSet<>()).add(b);
            map.computeIfAbsent(b, k -> new HashSet<>()).add(a);
        }
        return map;
    }

    private Map<T, int[]> calculateRecords(List<T> participants, List<Match<T>> matches) {
        Map<T, int[]> records = new HashMap<>();
        for (T p : participants) records.put(p, new int[]{0, 0});

        for (Match<T> m : matches) {
            if (m.getWinner() == null) continue;

            T a = m.getCompetitorA();
            T b = m.getCompetitorB();

            if (m.getWinner().equals(a)) {
                records.get(a)[0]++;
                records.get(b)[1]++;
            } else {
                records.get(b)[0]++;
                records.get(a)[1]++;
            }
        }
        return records;
    }

    private void ensureAllMatchesCompleted(List<List<Match<T>>> previousRounds) {
        for (int roundIndex = 0; roundIndex < previousRounds.size(); roundIndex++) {
            List<Match<T>> round = previousRounds.get(roundIndex);
            for (Match<T> match : round) {
                if (match.getWinner() == null) {
                    throw new IllegalStateException(
                            "Não é possível gerar a próxima rodada. "
                            + "A rodada " + (roundIndex + 1) + " ainda possui partidas sem resultado."
                    );
                }
            }
        }
    }

    private T findOpponentAvoidingRepeat(T a, List<T> group, Map<T, Set<T>> history) {
        for (T candidate : group) {
            if (!history.getOrDefault(a, Set.of()).contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private List<Match<T>> flatten(List<List<Match<T>>> rounds) {
        return rounds.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<Match<T>> generateNextRound(List<T> participants, List<Match<T>> previousMatches) {
        List<Match<T>> newMatches = new ArrayList<>();

        Map<T, int[]> records = calculateRecords(participants, previousMatches);
        List<T> activePlayers = getActivePlayers(participants, records);
        Map<String, List<T>> buckets = groupByRecord(activePlayers, records);

        pairBuckets(buckets, newMatches, previousMatches);
        return newMatches;
    }

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {

        validateParticipants(participants);
        ensureAllMatchesCompleted(previousRounds);

        List<Match<T>> flattened = flatten(previousRounds);
        List<Match<T>> newRound = generateNextRound(participants, flattened);

        if (newRound.isEmpty()) return previousRounds;

        List<List<Match<T>>> updated = new ArrayList<>(previousRounds);
        updated.add(newRound);
        return updated;
    }

    private List<T> getActivePlayers(List<T> participants, Map<T, int[]> records) {
        return participants.stream()
                .filter(p -> {
                    int[] r = records.get(p);
                    return r[0] < maxWins && r[1] < maxLosses;
                })
                .collect(Collectors.toList());
    }

    public int[] getRecordOf(T player, List<List<Match<T>>> rounds) {
        int wins = 0, losses = 0;

        for (List<Match<T>> r : rounds) {
            for (Match<T> m : r) {

                if (m.getWinner() == null) continue;

                if (m.getCompetitorA().equals(player)) {
                    if (m.getWinner().equals(player)) wins++; else losses++;
                }

                if (m.getCompetitorB().equals(player)) {
                    if (m.getWinner().equals(player)) wins++; else losses++;
                }
            }
        }
        return new int[]{wins, losses};
    }

    private Map<String, List<T>> groupByRecord(List<T> players, Map<T, int[]> records) {

        Map<String, List<T>> buckets = new HashMap<>();

        for (T p : players) {
            int[] r = records.get(p);
            String key = r[0] + "-" + r[1];
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        return buckets.entrySet().stream()
                .sorted((a, b) -> {
                    int[] ra = Arrays.stream(a.getKey().split("-")).mapToInt(Integer::parseInt).toArray();
                    int[] rb = Arrays.stream(b.getKey().split("-")).mapToInt(Integer::parseInt).toArray();

                if (ra[0] != rb[0]) return Integer.compare(rb[0], ra[0]);
                    return Integer.compare(ra[1], rb[1]);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private void pairBuckets(Map<String, List<T>> buckets,
                            List<Match<T>> output,
                            List<Match<T>> previousMatches) {

        Map<T, Set<T>> alreadyPlayed = buildHistory(previousMatches);

        for (List<T> group : buckets.values()) {

            group.sort(Comparator.comparing(Competitor::getName)); // ordem estável

            List<T> pool = new ArrayList<>(group);

            while (pool.size() >= 2) {

                T a = pool.remove(0);
                T b = findOpponentAvoidingRepeat(a, pool, alreadyPlayed);

                if (b == null) {
                    b = pool.remove(0);
                } else {
                    pool.remove(b);
                }

                output.add(createMatch.apply(a, b));
            }
        }
    }

    private void validateParticipants(List<T> participants) {
        if (participants == null || participants.isEmpty())
            throw new IllegalArgumentException("A lista de participantes está vazia.");

        Set<T> set = new HashSet<>(participants);
        if (set.size() != participants.size())
            throw new IllegalArgumentException("Existem participantes duplicados.");
    }

}
