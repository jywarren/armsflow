class FlowbaseController < ApplicationController
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @transfer_pages, @transfers = paginate :transfers, :per_page => 10
  end

  def show
    @transfer = Transfer.find(params[:id])
  end

  def year
    @year = params[:id]
    @transfers = Transfer.find(:all,:conditions => ["datetime = ? and value > 0 and recipient_name <> 'Unknown country'", @year])
  end
  
  def countryflow
    @country = params[:id]
    @year = params[:type]
    @transfers = Transfer.find(:all,:conditions => ["datetime= ? and value > 0 and agent_name = ? and recipient_name <> 'Unknown country'", @year,@country])
  end

  def full
    @range = (1950..2006).to_a
    @periods = []
    for period in @range
      @periods << Transfer.find(:all,:conditions => ["datetime = ? and value > 0 and recipient_name <> 'Unknown country'", period])   
    end
  end

  def years
    timeframe = params[:id].split("-")
    start = timeframe[0]
    finish = timeframe[1]
    @range = (start..finish).to_a
    @periods = []
    for period in @range
      @periods << Transfer.find(:all,:conditions => ["datetime = ? and value > 0", period])   
    end
  end
  
  def countrysum #by year for 1 country
    @range = (1950..2006).to_a
    @periods = []
    for period in @range
      transfers = Transfer.find(:all,:conditions => ["agent_name = ? and datetime = ? and value > 0", params[:id], period],:order => "agent_name ASC")
      sum = 0
      for transfer in transfers
        sum += transfer.value
      end
        @periods << sum
    end
  end
  
  def sums #sums for all countries
    @range = (1950..2006).to_a
    @periods = []
    for period in @range
      transfers = Transfer.find(:all,:conditions => ["datetime = ? and value > 0", period])
      sum = 0
      for transfer in transfers
        sum += transfer.value
      end
      if sum > 0
        @periods << sum
      end
    end
  end
  
  def country
    range = (1950..2006).to_a
    @periods = []
    for period in range
      @periods << Transfer.find(:all,:conditions => ["datetime = ? and value > 0 and agent_name = ?", period, params[:id]])
    end
  end

  def new
    @transfer = Transfer.new
  end

  def create
    @transfer = Transfer.new(params[:transfer])
    if @transfer.save
      flash[:notice] = 'Transfer was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @transfer = Transfer.find(params[:id])
  end

  def update
    @transfer = Transfer.find(params[:id])
    if @transfer.update_attributes(params[:transfer])
      flash[:notice] = 'Transfer was successfully updated.'
      redirect_to :action => 'show', :id => @transfer
    else
      render :action => 'edit'
    end
  end

  def destroy
    Transfer.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
