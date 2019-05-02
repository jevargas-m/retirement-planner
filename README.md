# Retirement-Planner
Final Project OMCIT-591

## Team Members
* Arley Sasson
* Enrique Vargas
* Xunjing Wu


## Project Description
The idea is to develop a retirement-planning tool for the average person that is thinking about retirement; it is not designed for investment professionals. Although we would like the user to see the impact of volatility on the final outcome of their retirement plan, we want to make it user-friendly with a decent amount of user interaction through a graphic user interface.
The program will ask the user for their age and their retirement goals, and how much money they can regularly contribute toward retirement. It will also include a brief survey to gauge user's risk appetite. Based on that information, the program will suggest a portfolio based on their goals and risk appetite. We plan on running a Monte Carlo simulation of the portfolio to present the user a projection of what their retirement income may look like in the future, and show scenarios of potential positive and negative outcomes based on the simulation.

## Key features
  * Recommend Equity/Bonds ratio for investment according to user profile
  * Read dataset in csv format with historic market returns for bonds and equity in order to estimate market volatility.
  * Build a normal probability distribution with market volatility 
  * Build randomized amortization tables using Monte Carlo simulation sampling investment returns from probability distribution
  * JUnit test for retirement calculations
  * Use JavaFX for GUI.  Leverage Graph components for displaying results
  * Leverage Apache Commons Math libraries for building probability distribution, statistical analysis of results and solver for estimating the right time to retire.

## Key modules
1 Risk profile questionnaire
  * Input: Answer to questions
  * Output: Risk score
  
2 Portfolio Builder
  * Input: Risk score, portfolio allocation to different investments.
  * Output: average and standard deviation of expected return
  
3 Simulation
  * Input: average and standard deviation of expected return.  Current age, retirement age, principal, monthly deposits, life expectancy
  * Output: probability of achieving certain amount of money in time
  
4 GUI

## Key User Inputs
* User risk profile through a questionnaire or selecting a pre-built portfolio
* Present amount of money
* Annual deposits
* Current age
* Desired retirement age
* Target yearly retirement

## Key Outputs
* If I retire today, how much money can I get on a yearly basis?
  * Provide Money and Probability, maybe as a cummulative prob graphgraph of money vs time
* When would I be able to retire? given I want to be X% sure (probability) that I will get at least X amount of money every year.
* What is my forecasted retirement? (e.g. how confident of retirement with a predefined amount of money at retirement age)

## API's
Using Apache Math Commons libraries for Descriptive Statistics and Frequency distributions
http://commons.apache.org/proper/commons-math/

## Dataset's
The construction of the portfolios that we are suggesting only consist of stocks and bonds and we change the percentage weight of 
equities or bonds depending on how risk averse the user is. We are obtaining monthly returns in both assets since 1993. 
For equity returns we are using the returns on the S&P 500  index ETF. Our source is Yahoo finance:
https://finance.yahoo.com/quote/SPY/history?period1=728283600&period2=1553832000&interval=1mo&filter=history&frequency=1mo  

For bond returns we are using the returns in the US Treasury 10yr bonds we are using the dataset from multpl.com:
https://www.multpl.com/10-year-treasury-rate/table/by-month


