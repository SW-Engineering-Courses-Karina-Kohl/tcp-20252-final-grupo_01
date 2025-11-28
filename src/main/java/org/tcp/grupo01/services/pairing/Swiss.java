package org.tcp.grupo01.services.pairing;

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

    private void validateParticipants(List<T> participants) {
        if (participants == null || participants.isEmpty())
            throw new IllegalArgumentException("A lista de participantes está vazia.");

        if (participants.size() != 16 && participants.size() != 32)
            throw new IllegalArgumentException("Swiss só permite 16 ou 32 participantes.");

        if (participants.size() % 2 != 0)
            throw new IllegalArgumentException("Swiss exige número par de participantes.");

        Set<T> set = new HashSet<>(participants);
        if (set.size() != participants.size())
            throw new IllegalArgumentException("Existem participantes duplicados.");
    }

    private void ensureAllMatchesCompleted(List<List<Match<T>>> rounds) {
        for (int r = 0; r < rounds.size(); r++) {
            for (Match<T> m : rounds.get(r)) {
                if (m.getWinner() == null)
                    throw new IllegalStateException("A rodada " + (r + 1) + " ainda possui partidas incompletas.");
            }
        }
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

    private List<T> getActivePlayers(List<T> participants, Map<T, int[]> recs) {
        return participants.stream()
                .filter(p -> {
                    int[] r = recs.get(p);
                    return r[0] < maxWins && r[1] < maxLosses;
                })
                .collect(Collectors.toList());
    }

    private Map<String, List<T>> groupByRecord(List<T> players, Map<T, int[]> recs) {
        Map<String, List<T>> buckets = new LinkedHashMap<>();

        for (T p : players) {
            int[] r = recs.get(p);
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
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
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

    private T findNewOpponent(T a, List<T> group, Map<T, Set<T>> history) {
        for (T b : group)
            if (!history.getOrDefault(a, Set.of()).contains(b))
                return b;

        return group.get(0);
    }

    private List<Match<T>> generateNextRound(List<T> participants, List<Match<T>> prevMatches) {

        Map<T, int[]> recs = calculateRecords(participants, prevMatches);
        List<T> actives = getActivePlayers(participants, recs);

        if (actives.size() < 2) return List.of();

        Map<String, List<T>> buckets = groupByRecord(actives, recs);
        Map<T, Set<T>> history = buildHistory(prevMatches);

        List<Match<T>> newRound = new ArrayList<>();

        for (List<T> group : buckets.values()) {

            group.sort(Comparator.comparing(Competitor::getName));

            if (group.size() % 2 != 0)
                throw new IllegalStateException("Bucket " + group + " está ímpar! Isso não pode acontecer com 16/32 jogadores.");

            List<T> pool = new ArrayList<>(group);

            while (pool.size() >= 2) {
                T a = pool.remove(0);
                T b = findNewOpponent(a, pool, history);
                pool.remove(b);

                Match<T> match = createMatch.apply(a, b);
                newRound.add(match);
            }
        }

        return newRound;
    }

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {

        validateParticipants(participants);
        ensureAllMatchesCompleted(previousRounds);

        List<Match<T>> allMatches = previousRounds.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<Match<T>> newRound = generateNextRound(participants, allMatches);

        if (newRound.isEmpty()) return previousRounds;

        List<List<Match<T>>> updated = new ArrayList<>(previousRounds);
        updated.add(newRound);
        return updated;
    }

    public int[] getRecordOf(Competitor player, List<List<Match<?>>> rounds) {
        int wins = 0, losses = 0;

        for (List<Match<?>> r : rounds) {
            for (Match<?> m : r) {
                if (m.getWinner() == null) continue;
                if (m.getWinner().equals(player)) wins++;
                else if (m.getCompetitorA().equals(player) || m.getCompetitorB().equals(player)) losses++;
            }
        }
        return new int[]{wins, losses};
    }

}
