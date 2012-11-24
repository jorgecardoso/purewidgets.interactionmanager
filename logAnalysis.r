logs <- read.table("/Users/jorge/Google Drive/PhD/Work/0-Code-EclipseWorkspace/InteractionManager/dates.csv", sep="#")

t <- as.character(strptime(logs[,], "%d/%b/%Y:%I:%M:%S %z"))
t <- t[!is.na(t)]

#Filter by dates
t <- t[t >= "2012-10-18" ]
t <- t[t <= "2012-10-19" ]

#t <- strftime(t, format="%Y-%B-%d %H:%M")
t <- strftime(t, format="%Y-%B-%d %H")
hist(table(t))