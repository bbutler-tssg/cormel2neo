function plotTimings(dataFile)
timings = dlmread(dataFile,",",0,0);
plot(timings(:,2),timings(:,3)/1e+6)
step=max(diff(timings(:,2)));
xlabel("Record number")
ylabel(["milliseconds per " int2str(step) " records uploaded"])
title("Timing data, for uploading Cormel records to neo4j")
plotFile = [substr(dataFile, 1, length(dataFile)-4) ".pdf"];
print(plotFile, "-dpdf")
