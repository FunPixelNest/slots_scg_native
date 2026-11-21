"""
Coin economy model: free coins given per hour vs coins burned per hour, by player level.

Mirrors engine/src/main/kotlin/.../Economy.kt. Run: python3 tools/economy/model.py

Target (product rule): coins burned = half of the free coins given, for a player who bets the
minimum of their level. Players who bet higher burn more than that.
"""
BETS = [10_000, 20_000, 50_000, 100_000, 200_000, 500_000, 1_000_000, 2_000_000, 5_000_000,
        10_000_000, 20_000_000, 50_000_000, 100_000_000]
WINDOW = 5                       # bet steps open at once
MIN_STEP_LEVELS = [1, 5, 10, 15, 20, 30, 40, 50, 70]  # level where min bet moves up one step

SPINS_PER_HOUR = 400             # realistic incl. win animations
HOUSE_EDGE = 0.065               # machines average ~93.5% RTP
HOURS_PER_DAY = 2                # daily bonus + wheel + tasks spread over this much play
WIN_RATE_FOR_XP = 0.35           # share of spins that pay at least the bet

HOURLY_UNITS = 6
VIDEO_UNITS, VIDEOS_PER_HOUR = 7, 2
DAILY_UNITS = [5, 8, 10, 12, 15, 20, 30]
WHEEL = [(3, 22), (5, 20), (4, 20), (12, 10), (8, 14), (25, 6), (3, 22), (50, 2)]
TASK_UNITS = [1, 1, 2, 2, 3]


def min_step(level):
    return sum(1 for l in MIN_STEP_LEVELS if level >= l) - 1


def unit(level):
    return BETS[min_step(level)]


def xp_for_level(level):
    return {1: 3, 2: 5, 3: 8, 4: 12}.get(level, 9 * level)


def level_up_units(level):
    return max(2.0, round(level * 0.3, 1))


def per_hour(level):
    u = unit(level)
    burn = SPINS_PER_HOUR * u * HOUSE_EDGE
    xp_per_hour = SPINS_PER_HOUR * (1 + WIN_RATE_FOR_XP)  # at min bet: 1 XP, 2 on a win
    levels_per_hour = xp_per_hour / xp_for_level(level)
    wheel_ev = sum(v * w for v, w in WHEEL) / sum(w for _, w in WHEEL)
    daily = (sum(DAILY_UNITS) / len(DAILY_UNITS) + wheel_ev + sum(TASK_UNITS)) / HOURS_PER_DAY
    give_units = HOURLY_UNITS + VIDEO_UNITS * VIDEOS_PER_HOUR + levels_per_hour * level_up_units(level) + daily
    return u, burn, give_units * u, levels_per_hour


if __name__ == "__main__":
    print(f"{'level':>5} {'min bet':>10} {'given/h':>12} {'burned/h':>12} {'burn/give':>9} {'levels/h':>8}")
    for lv in [1, 3, 5, 8, 10, 15, 20, 30, 40, 50, 70, 100]:
        u, burn, give, lph = per_hour(lv)
        print(f"{lv:>5} {u:>10,} {give:>12,.0f} {burn:>12,.0f} {burn / give:>9.2f} {lph:>8.1f}")
