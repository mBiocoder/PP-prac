###############################################################
#Boxplot visualizations for Q3/ SOV scores from cross-validation#

library(ggpubr)
library(data.table)
library(gridExtra)
library(ggplot2)

#load statistics by GOR version and fold

#Gor 1
content_f1_g1 <- readLines("C:/Users/hanna/Desktop/data/fold1_Gor1_detail_validation.txt")
indices_f1_g1 <- grep(">", substr(content_f1_g1, 1,50))  
table_f1_g1 <- read.table(text=content_f1_g1[indices_f1_g1], na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f2_g1 <- readLines("C:/Users/hanna/Desktop/data/fold2_Gor1_detail_validation.txt")
indices_f2_g1 <- grep(">", substr(content_f2_g1, 1,50))  
table_f2_g1 <- read.table(text=content_f2_g1[indices_f2_g1], na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f3_g1 <- readLines("C:/Users/hanna/Desktop/data/fold3_Gor1_detail_validation.txt")
indices_f3_g1 <- grep(">", substr(content_f3_g1, 1,50))  
table_f3_g1 <- read.table(text=content_f3_g1[indices_f3_g1],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f4_g1 <- readLines("C:/Users/hanna/Desktop/data/fold4_Gor1_detail_validation.txt")
indices_f4_g1 <- grep(">", substr(content_f4_g1, 1,50))  
table_f4_g1 <- read.table(text=content_f4_g1[indices_f4_g1],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f5_g1 <- readLines("C:/Users/hanna/Desktop/data/fold5_Gor1_detail_validation.txt")
indices_f5_g1 <- grep(">", substr(content_f5_g1, 1,50))  
table_f5_g1 <- read.table(text=content_f5_g1[indices_f5_g1],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

#Gor 3
content_f1_g3 <- readLines("C:/Users/hanna/Desktop/data/fold1_Gor3_detail_validation.txt")
indices_f1_g3 <- grep(">", substr(content_f1_g3, 1,50))  
table_f1_g3 <- read.table(text=content_f1_g3[indices_f1_g3],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f2_g3 <- readLines("C:/Users/hanna/Desktop/data/fold2_Gor3_detail_validation.txt")
indices_f2_g3 <- grep(">", substr(content_f2_g3, 1,50))  
table_f2_g3 <- read.table(text=content_f2_g3[indices_f2_g3],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f3_g3 <- readLines("C:/Users/hanna/Desktop/data/fold3_Gor3_detail_validation.txt")
indices_f3_g3 <- grep(">", substr(content_f3_g3, 1,50))  
table_f3_g3 <- read.table(text=content_f3_g3[indices_f3_g3],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f4_g3 <- readLines("C:/Users/hanna/Desktop/data/fold4_Gor3_detail_validation.txt")
indices_f4_g3 <- grep(">", substr(content_f4_g3, 1,50))  
table_f4_g3 <- read.table(text=content_f4_g3[indices_f4_g3],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f5_g3 <- readLines("C:/Users/hanna/Desktop/data/fold5_Gor3_detail_validation.txt")
indices_f5_g3 <- grep(">", substr(content_f5_g3, 1,50))  
table_f5_g3 <- read.table(text=content_f5_g3[indices_f5_g3],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

#Gor 4
content_f1_g4 <- readLines("C:/Users/hanna/Desktop/data/fold1_Gor4_detail_validation.txt")
indices_f1_g4 <- grep(">", substr(content_f1_g4, 1,50))  
table_f1_g4 <- read.table(text=content_f1_g4[indices_f1_g4],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f2_g4 <- readLines("C:/Users/hanna/Desktop/data/fold2_Gor4_detail_validation.txt")
indices_f2_g4 <- grep(">", substr(content_f2_g4, 1,50))  
table_f2_g4 <- read.table(text=content_f2_g4[indices_f2_g4],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f3_g4 <- readLines("C:/Users/hanna/Desktop/data/fold3_Gor4_detail_validation.txt")
indices_f3_g4 <- grep(">", substr(content_f3_g4, 1,50))  
table_f3_g4 <- read.table(text=content_f3_g4[indices_f3_g4],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f4_g4 <- readLines("C:/Users/hanna/Desktop/data/fold4_Gor4_detail_validation.txt")
indices_f4_g4 <- grep(">", substr(content_f4_g4, 1,50))  
table_f4_g4 <- read.table(text=content_f4_g4[indices_f4_g4],  na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

content_f5_g4 <- readLines("C:/Users/hanna/Desktop/data/fold5_Gor4_detail_validation.txt")
indices_f5_g4 <- grep(">", substr(content_f5_g4, 1,50))  
table_f5_g4 <- read.table(text=content_f5_g4[indices_f5_g4], na.strings = "N.A.", header = FALSE, col.names = c("ID", "Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"))

#Data processing for analysis
table_gor1 <- rbind(table_f1_g1, table_f2_g1, table_f3_g1, table_f4_g1,  table_f5_g1)
table_gor3 <- rbind(table_f1_g3, table_f2_g3, table_f3_g3, table_f4_g3,  table_f5_g3)
table_gor4 <- rbind(table_f1_g4, table_f2_g4, table_f3_g4, table_f4_g4,  table_f5_g4)

table_gor1$gor_version <- "GOR1"
table_gor3$gor_version <- "GOR3"
table_gor4$gor_version <- "GOR4"

table_gors <- rbind(table_gor1, table_gor3, table_gor4)

comp_list <- list(c("GOR1","GOR3"), c("GOR1","GOR4"), c("GOR3","GOR4"))

compare_means(c(Q3,SOV) ~ gor_version, data=table_gors, paired = TRUE)

#ggboxplot(table_gors, y="Q3", x="gor_version", xlab = "GOR Version") + stat_compare_means(comparisons = comp_list, paired = TRUE)

#ggboxplot(table_gors, y="SOV", x="gor_version", xlab = "GOR Version") + stat_compare_means(comparisons = comp_list, paired = TRUE)

c_pal <- c("#546ba9", "#468b4f", "#aa8db1")

#Visualize boxplots
ggboxplot(table_gors, y=c("SOV","Q3"), combine = TRUE, x="gor_version", xlab = "GOR Version", ylab = "Score", title = "Comparative Analysis of Gor I-IV", color = "gor_version", palette = get_palette(c_pal, 3)) + 
  stat_compare_means(comparisons = comp_list, paired = TRUE) +  theme(plot.title = element_text(hjust = 0.5)) + font("title", size = 30)

#Further visualization for in-depth analysis
table_gor1_new <- table_gor1
setDT(table_gor1_new) 
table_gor1_new[,gor_version := NULL]
table_gor3_new <- table_gor3 
setDT(table_gor3_new)
table_gor3_new[,gor_version := NULL]
table_gor4_new <- table_gor4 
setDT(table_gor4_new)
table_gor4_new[,gor_version := NULL]

colnames(table_gor1_new) <- c("ID_1", "Q3_1", "SOV_1", "QH_1", "QE_1", "QC_1", "SOVH_1", "SOVE_1", "SOVC_1")
colnames(table_gor3_new) <- c("ID_3", "Q3_3", "SOV_3", "QH_3", "QE_3", "QC_3", "SOVH_3", "SOVE_3", "SOVC_3")
colnames(table_gor4_new) <- c("ID_4", "Q3_4", "SOV_4", "QH_4", "QE_4", "QC_4", "SOVH_4", "SOVE_4", "SOVC_4")

#str(table_gor1_new)

table_gors_new <- merge(table_gor1_new, table_gor3_new, by.x="ID_1", by.y="ID_3")
table_gors_new <- merge(table_gors_new, table_gor4_new, by.x="ID_1", by.y="ID_4")

##Plotting: Scatterplots

plot1 <- ggplot(table_gors_new, aes(x=SOV_1, y=SOV_3)) + geom_point(color = "#546ba9") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR I") + ylab("GOR III")
plot2 <- ggplot(table_gors_new, aes(x=Q3_1, y=Q3_3)) + geom_point(color = "#993a3a") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR I") + ylab("GOR III") 

plot3 <- ggplot(table_gors_new, aes(x=SOV_1, y=SOV_4)) + geom_point(color = "#546ba9") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR I") + ylab("GOR IV") 
plot4 <- ggplot(table_gors_new, aes(x=Q3_1, y=Q3_4)) + geom_point(color = "#993a3a") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR I") + ylab("GOR IV") 

plot5 <- ggplot(table_gors_new, aes(x=SOV_3, y=SOV_4)) + geom_point(color = "#546ba9") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR III") + ylab("GOR IV") 
plot6 <- ggplot(table_gors_new, aes(x=Q3_3, y=Q3_4)) + geom_point(color = "#993a3a") + geom_abline(intercept = 0, slope = 1) + scale_y_continuous(limits = c(0,100)) + scale_x_continuous(limits = c(0,100)) + xlab("GOR III") + ylab("GOR IV") 

grid.arrange(plot1,plot2,plot3,plot4,plot5,plot6, nrow = 3)

#Histograms for the distribution in differences in performance
###Differences: SOV

table_gors_new$diff_sov_1_3 <- table_gors_new$SOV_1 - table_gors_new$SOV_3
table_gors_new$diff_sov_1_4 <- table_gors_new$SOV_1 - table_gors_new$SOV_4
table_gors_new$diff_sov_3_4 <- table_gors_new$SOV_3 - table_gors_new$SOV_4

hist_sov1 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_sov_1_3), fill = "#546ba9", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR I - III")
hist_sov2 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_sov_1_4), fill = "#546ba9", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR I - IV")
hist_sov3 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_sov_3_4), fill = "#546ba9", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR III - IV")

###Differences: Q3
table_gors_new$diff_Q3_1_3 <- table_gors_new$Q3_1 - table_gors_new$Q3_3
table_gors_new$diff_Q3_1_4 <- table_gors_new$Q3_1 - table_gors_new$Q3_4
table_gors_new$diff_Q3_3_4 <- table_gors_new$Q3_3 - table_gors_new$Q3_4

hist_q3_1 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_Q3_1_3), fill = "#993a3a", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR I - III")
hist_q3_2 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_Q3_1_4), fill = "#993a3a", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR I - IV")
hist_q3_3 <- ggplot(table_gors_new) + geom_histogram(aes(x=diff_Q3_3_4), fill = "#993a3a", binwidth = 2) + geom_vline(xintercept = 0, size=1.5) + scale_y_continuous(limits = c(0,110)) + scale_x_continuous(limits = c(-100,100)) + labs(x="Diff GOR III - IV")

grid.arrange(hist_sov1, hist_sov2, hist_sov3, hist_q3_1, hist_q3_2, hist_q3_3, nrow=2)


##Bar chart 

#c_p <- c("#003366", "#99adc1", "#814648", "#cc9999")

#values <- c(64.27, 64.38, 63.05, 62.8, 65.62, 73.56, 52.58, 50.70, 47.82, 58.23, 47.43, 48.87)
#metric <- c(rep("Q3_means",3),rep("Q3_ref",3),rep("SOV_means",3),rep("SOV_ref",3))
#gor_vers <- c(rep(c("I","III","IV"),4)) 

#Q3_ref = c(62.8, 65.62, 73.56)
#sov_means <- c(52.58, 50.70, 47.82)
#sov_ref <- c(58.23, 47.43, 48.87)

#df <- data.frame("values" = values, "metric" = metric, "gor_vers" = gor_vers)

#ggplot(df, aes(x=gor_vers, y=values, group = metric, fill = metric)) + geom_bar(stat="identity", position = "dodge") + 
#  scale_fill_manual(values=c_p, name = "Performance Scores", labels = c("Cross Validation Q3", "Reference Prediction Q3","Cross Validation SOV", "Reference Prediction SOV")) + 
#  labs(x="GOR Version", y="Score", title = "Comparison of Performance with Reference Predictions (CB513 dataset)") + theme(plot.title = element_text(size=30), 
#                                                                                                          legend.text = element_text(size=20), legend.title = element_text(size=20), axis.text = element_text(size=20), axis.title = element_text(size=20))

