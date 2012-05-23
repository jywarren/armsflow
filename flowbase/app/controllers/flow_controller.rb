class FlowController < ApplicationController
  def index
    @urlbase = "http://www.armsflow.org"
#    @urlbase = "http://localhost:3000"
    if params[:id].blank?
      @year = 2006
    else
      @year = params[:id]
    end
    if @year.to_i > 2006
      @year = 2006
    elsif @year.to_i < 1950
      @year = 1950
    end
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def year
    redirect_to :action => 'index', :id => params[:id]
  end
  
  def country
    @urlbase = "http://www.armsflow.org"
#    @urlbase = "http://localhost:3000"
    if params[:type].blank?
      @year = 2006
    else
      @year = params[:type]
    end
    if @year.to_i > 2006
      @year = 2006
    elsif @year.to_i < 1950
      @year = 1950
    end
    if params[:type].blank?
      #flash[:notice] = "No date specified. "+@year.to_s+" is the default."
    end
    @country = params[:id]
    @exports = Transfer.find(:all,:conditions => ["agent_name = ? and datetime = ? and value > 0", @country, @year],:order => "agent_name ASC")
    @imports = Transfer.find(:all,:conditions => ["recipient_name = ? and datetime = ? and value > 0", @country, @year],:order => "agent_name ASC")
    @year = @year.to_s
  end

  # def year
  #   @year = Time.utc(params[:id]).strftime("%Y")
  #   start = Time.utc(params[:id].to_i-1).strftime("%Y-%m-%d 00:%M:%S")
  #   finish = Time.utc(params[:id].to_i).strftime("%Y-12-31 23:59:59")
  #   @transfers = Transfer.find(:all,:conditions => ["datetime > ? and datetime < ? and value > 0", start, finish],:order => "agent_name ASC")
  # end
  # 
  # def years
  #   timeframe = params[:id].split("-")
  #   start = timeframe[0]
  #   finish = timeframe[1]
  #   @range = (start..finish).to_a
  #   @periods = []
  #   for period in @range
  #     period = period.to_i
  #     pstart = Time.utc(period.to_i-1).strftime("%Y-%m-%d 00:%M:%S")
  #     pfinish = Time.utc(period.to_i).strftime("%Y-12-31 23:59:59")
  #     @periods << Transfer.find(:all,:conditions => ["datetime >= ? and datetime <= ? and value > 0", pstart, pfinish],:order => "agent_name ASC")   
  #   end
  # end
  # 
  # # def countryAll
  #   @country = params[:id]
  #   @range = (1950..2006).to_a
  #   @periods = []
  #   for period in @range
  #     period = period.to_i
  #     pstart = Time.utc(period.to_i-1).strftime("%Y-%m-%d 00:%M:%S")
  #     pfinish = Time.utc(period.to_i).strftime("%Y-12-31 23:59:59")
  #     @periods << Transfer.find(:all,:conditions => ["datetime >= ? and datetime <= ? and value > 0 and agent_name = ?", pstart, pfinish, params[:id]],:order => "agent_name ASC")
  #   end
  # end
  # 
  # 
  # 
  # def countrybycountry
  #   @country = params[:id]
  #   @range = (1950..2006).to_a
  #   @agents = Agent.find(:all,:order => "name ASC")
  #   @transfers = Transfer.find(:all,:conditions => ["value > 0 and agent_name = ?", params[:id]],:order => "agent_name ASC")
  #   @countries = []
  #   for agent in @agents
  #     @countries[agent.id] = []
  #     for transfer in @transfers
  #       if transfer.recipient_id == agent.id
  #         @countries[agent.id] << transfer
  #       end
  #     end
  #   end
  # end

end
